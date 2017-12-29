(function() {
    'use strict';
    angular
        .module('cadastroEverycareApp')
        .factory('Professional', Professional);

    Professional.$inject = ['$resource'];

    function Professional ($resource) {
        var resourceUrl =  'api/professionals/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
