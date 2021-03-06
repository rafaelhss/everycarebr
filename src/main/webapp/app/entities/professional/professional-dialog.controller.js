(function() {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .controller('ProfessionalDialogController', ProfessionalDialogController);

    ProfessionalDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Professional', 'Answer'];

    function ProfessionalDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Professional, Answer) {
        var vm = this;

        vm.professional = entity;
        vm.clear = clear;
        vm.save = save;
        vm.answers = Answer.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.professional.id !== null) {
                Professional.update(vm.professional, onSaveSuccess, onSaveError);
            } else {
                Professional.save(vm.professional, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cadastroEverycareApp:professionalUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
