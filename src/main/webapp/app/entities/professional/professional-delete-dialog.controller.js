(function() {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .controller('ProfessionalDeleteController',ProfessionalDeleteController);

    ProfessionalDeleteController.$inject = ['$uibModalInstance', 'entity', 'Professional'];

    function ProfessionalDeleteController($uibModalInstance, entity, Professional) {
        var vm = this;

        vm.professional = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Professional.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
