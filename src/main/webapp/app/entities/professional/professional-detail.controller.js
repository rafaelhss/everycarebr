(function() {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .controller('ProfessionalDetailController', ProfessionalDetailController);

    ProfessionalDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Professional', 'Answer'];

    function ProfessionalDetailController($scope, $rootScope, $stateParams, previousState, entity, Professional, Answer) {
        var vm = this;

        vm.professional = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cadastroEverycareApp:professionalUpdate', function(event, result) {
            vm.professional = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
