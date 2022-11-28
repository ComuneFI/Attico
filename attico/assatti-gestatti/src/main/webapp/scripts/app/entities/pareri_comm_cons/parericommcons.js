'use strict';

angular.module('cifra2gestattiApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('commissioniCons', {
                parent: 'entity',
                url: '/commissioniCons',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.commissioniCons.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/pareri_comm_cons/parericommcons.html',
                        controller: 'CommissioniConsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('commissioniCons');
                        $translatePartialLoader.addPart('documentoInformatico');
                        return $translate.refresh();
                    }]
                }
            });
    });
