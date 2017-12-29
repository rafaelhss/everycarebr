(function() {
    'use strict';

    angular
        .module('cadastroEverycareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('professional', {
            parent: 'entity',
            url: '/professional?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Professionals'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/professional/professionals.html',
                    controller: 'ProfessionalController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('professional-detail', {
            parent: 'professional',
            url: '/professional/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Professional'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/professional/professional-detail.html',
                    controller: 'ProfessionalDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Professional', function($stateParams, Professional) {
                    return Professional.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'professional',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('professional-detail.edit', {
            parent: 'professional-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professional/professional-dialog.html',
                    controller: 'ProfessionalDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Professional', function(Professional) {
                            return Professional.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('professional.new', {
            parent: 'professional',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professional/professional-dialog.html',
                    controller: 'ProfessionalDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                telegramId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('professional', null, { reload: 'professional' });
                }, function() {
                    $state.go('professional');
                });
            }]
        })
        .state('professional.edit', {
            parent: 'professional',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professional/professional-dialog.html',
                    controller: 'ProfessionalDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Professional', function(Professional) {
                            return Professional.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('professional', null, { reload: 'professional' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('professional.delete', {
            parent: 'professional',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/professional/professional-delete-dialog.html',
                    controller: 'ProfessionalDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Professional', function(Professional) {
                            return Professional.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('professional', null, { reload: 'professional' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
