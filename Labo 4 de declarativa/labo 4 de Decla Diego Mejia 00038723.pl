% Ejercicio 3

almacenar(N, L) :-
    digitos(N, L).

% Caso base
digitos(N, [N]) :-
    N < 10.

% Caso recursivo
digitos(N, [D|R]) :-
    N >= 10,
    D is N mod 10,
    N1 is N // 10,
    digitos(N1, R).
