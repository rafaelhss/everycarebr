package br.com.everycare.everycare.gateway.service;

import br.com.everycare.domain.Answer;
import br.com.everycare.domain.Professional;
import br.com.everycare.domain.Question;
import br.com.everycare.everycare.gateway.telegram.Util.TelegramUtil;
import br.com.everycare.everycare.gateway.telegram.config.TelegramConfig;
import br.com.everycare.everycare.gateway.telegram.model.Update;
import br.com.everycare.everycare.gateway.telegram.sender.Sender;
import br.com.everycare.repository.AnswerRepository;
import br.com.everycare.repository.ProfessionalRepository;
import br.com.everycare.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafael on 27/10/17.
 */
@Service
public class UpdateService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private QuestionRepository  questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TelegramUtil telegramUtil;


    public void processUpdate(Update update){

        Question question = questionRepository.findTop1ByOrderByOrdNumAsc();


            //Acha o profissional
        String telegramId = update.getMessage().getFrom().getId().toString();
        Professional professional = professionalRepository.findByTelegramId(telegramId);

        professional = processaRestart(update, professional);

        //se o profissional nao existir
        if(professional == null){
            //cria o profissional
            professional = new Professional();
            String first_name = update.getMessage().getFrom().getFirst_name();
            String last_name = update.getMessage().getFrom().getLast_name();
            professional.setName(first_name + " " + last_name);

            professional.setTelegramId(telegramId);

            professional = professionalRepository.save(professional);

        } else {
            //se o profissional existir

                Question currentQuestion = question;
               //descobre qual pergunta parou
               List<Answer> professionalAnswers = answerRepository.findByProfessional(professional);

               Optional<Integer> lastAnswerQuestionOrder = professionalAnswers.stream()
                   .map(answer -> answer.getQuestion().getOrdNum())
                   .reduce(Integer::max);

               if(lastAnswerQuestionOrder.isPresent()){
                   currentQuestion = questionRepository.findTop1ByOrdNumGreaterThanOrderByOrdNumAsc(lastAnswerQuestionOrder.get());
               }

               if(currentQuestion != null) {
                   //grava a resposta
                   String text = update.getMessage().getText();
                   Answer answer = new Answer();
                   answer.setProfessional(professional);
                   answer.setQuestion(currentQuestion);
                   answer.setText(text);


                   //Photo
                   try {
                       byte[] imageBytes = telegramUtil.getPicture(update);
                       answer.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imageBytes)));
                       answer.setPhotoContentType("image/jpg");
                   } catch (Exception e){
                       System.out.println("Provavelmente nao tem imagem....");
                       e.printStackTrace();
                   }

                   answerRepository.save(answer);

                   //pega a proxima pergunta
                   question = questionRepository.findTop1ByOrdNumGreaterThanOrderByOrdNumAsc(currentQuestion.getOrdNum());

               } else {
                   //provavelmente acabou o questionario
                   question = null;
               }
        }

        String text = "Fim do questionario";
        if(question != null) {
            text = question.getText();
            text = text.replace("@@NOME@@", professional.getName());
        }
        //faz a pergunta
        try {
            new Sender(TelegramConfig.BOT_ID).sendResponse(update.getMessage().getFrom().getId(), text);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Professional processaRestart(Update update, Professional professional) {
        if(update.getMessage().getText()!= null && update.getMessage().getText().toLowerCase().trim().equals("restart")){
            answerRepository.findByProfessional(professional).forEach(answer -> answerRepository.delete(answer));
            professionalRepository.delete(professional);
            return null;
        }
        return professional;

    }


}
