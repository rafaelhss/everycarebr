(function () {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
