#lang racket

; Funcion para calcular el interes del deposito
(define (calcular-interes deposito)
  (cond
    [(<= deposito 40000) 0.035]
    [(<= deposito 260000) 0.04]
    [else 0.045]))

; Pruebas de la funcion
(displayln (calcular-interes 30000))
(displayln (calcular-interes 100000))
(displayln (calcular-interes 300000))
