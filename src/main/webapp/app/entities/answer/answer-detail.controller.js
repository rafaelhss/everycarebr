(function() {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .controller('AnswerDetailController', AnswerDetailController);

    AnswerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Answer', 'Question', 'Professional'];

    function AnswerDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Answer, Question, Professional) {
        var vm = this;

        vm.answer = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('cadastroEverycareApp:answerUpdate', function(event, result) {
            vm.answer = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
