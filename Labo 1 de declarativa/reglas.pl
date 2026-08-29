:- consult('hechos.pl').

adulto(Persona) :-
    edad(Persona, Edad),
    Edad >= 18.

armado(Persona) :-
    tiene_arma(Persona, _).

lugar_peligroso(Lugar) :-
    dificultad(Lugar, alta);
    dificultad(Lugar, muy_alta).

enemigo_plaga(Enemigo) :-
    enemigo(Enemigo),
    infectado_por(Enemigo, las_plagas).