'use strict';

angular.module('cifra2gestattiApp').config(function ($stateProvider) {
        $stateProvider
            .state('ordineGiorno', {
                parent: 'entity',
                url: '/ordineGiorno',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.ordineGiorno.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ordineGiorno/ordineGiornos.html',
                        controller: 'OrdineGiornoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ordineGiorno');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ordineGiornoDetail', {
                parent: 'entity',
                url: '/ordineGiorno/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.atto.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ordineGiorno/ordineGiorno-detail.html',
                        controller: 'OrdineGiornoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ordineGiorno');
						$translatePartialLoader.addPart('aoo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ordineGiornoDetailSection', {
                parent: 'entity',
                url: '/ordineGiorno/:id/:section',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.ordineGiorno.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ordineGiorno/ordineGiorno-detail.html',
                        controller: 'OrdineGiornoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ordineGiorno');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ordineGiornoGiacenza', {
                parent: 'entity',
                url: '/giacenza/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.ordineGiorno.detail.titlegiacenza'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ordineGiorno/giacenza.html',
                        controller: 'GiacenzaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ordineGiorno');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ordineGiornoSospesi', {
                parent: 'entity',
                url: '/sospesi',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cifra2gestattiApp.ordineGiorno.detail.titlegiacenza'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ordineGiorno/sospesi.html',
                        controller: 'SospesiController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ordineGiorno');
                        return $translate.refresh();
                    }]
                }
            });
    });

angular.module('cifra2gestattiApp').directive('aDisabled', function() {
    return {
        compile: function(tElement, tAttrs, transclude) {
            //Disable ngClick
            tAttrs["ngClick"] = "!("+tAttrs["aDisabled"]+") && ("+tAttrs["ngClick"]+")";

            //return a link function
            return function (scope, iElement, iAttrs) {

                //Toggle "disabled" to class when aDisabled becomes true
                scope.$watch(iAttrs["aDisabled"], function(newValue) {
                    if (newValue !== undefined) {
                        iElement.toggleClass("disabled", newValue);
                    }
                });

                //Disable href on click
                iElement.on("click", function(e) {
                    if (scope.$eval(iAttrs["aDisabled"])) {
                        e.preventDefault();
                    }
                });
            };
        }
    };
});
