% ---------- DERIVADAS ----------

% Derivada de una constante
d(Var, F, 0) :-
    atomic(Var), number(F), !.

% Derivada de una variable respecto a si misma
d(Var, F, 1) :-
    Var == F, !.

% Derivada de una suma
d(Var, A+B, R1+R2) :-
    d(Var, A, R1),
    d(Var, B, R2), !.

% Derivada de una resta
d(Var, A-B, R1-R2) :-
    d(Var, A, R1),
    d(Var, B, R2), !.

% Derivada de K*x^N
d(Var, K*Var^N, C*Var^Nm1) :-
    atomic(Var),
    number(K), number(N),
    is(C, K*N),
    is(Nm1, N-1), !.

% Derivada de una constante por una variable
d(Var, K*Var, K) :-
    atomic(Var), number(K), !.

% Derivada de x^N
d(Var, Var^N, N*Var^Nm1) :-
    number(N),
    is(Nm1, N-1), !.

% Derivada del logaritmo natural
d(Var, log(Var), 1/Var) :-
    atomic(Var), !.


% ---------- INTEGRALES ----------

% Integral de una suma
s(Var, A+B, R1+R2) :-
    s(Var, A, R1),
    s(Var, B, R2), !.

% Integral de una resta
s(Var, A-B, R1-R2) :-
    s(Var, A, R1),
    s(Var, B, R2), !.

% Integral de K*x^N
s(Var, K*Var^N, K/M*Var^M) :-
    atomic(Var),
    number(K), number(N),
    N =\= -1,
    is(M, N+1), !.

% Integral de una constante
s(Var, K, K*Var) :-
    atomic(Var), number(K), !.

% Integral de x respecto a x
s(Var, Var, 1/2*Var^2) :-
    atomic(Var), !.

% Integral de x^N
s(Var, Var^N, 1/M*Var^M) :-
    number(N),
    N =\= -1,
    is(M, N+1), !.


% ---------- EVALUACION NUMERICA ----------

% Constantes
eval_expr(_, E, E) :-
    number(E), !.

% Variable x evaluada en un punto
eval_expr(P, x, P) :-
    number(P), !.

% Suma
eval_expr(P, A+B, R) :-
    eval_expr(P, A, RA),
    eval_expr(P, B, RB),
    R is RA+RB, !.

% Resta
eval_expr(P, A-B, R) :-
    eval_expr(P, A, RA),
    eval_expr(P, B, RB),
    R is RA-RB, !.

% Multiplicacion
eval_expr(P, A*B, R) :-
    eval_expr(P, A, RA),
    eval_expr(P, B, RB),
    R is RA*RB, !.

% Division
eval_expr(P, A/B, R) :-
    eval_expr(P, A, RA),
    eval_expr(P, B, RB),
    RB =\= 0,
    R is RA/RB, !.

% Potencia
eval_expr(P, A^B, R) :-
    eval_expr(P, A, RA),
    eval_expr(P, B, RB),
    R is RA^RB, !.

% Logaritmo natural
eval_expr(P, log(A), R) :-
    eval_expr(P, A, RA),
    RA > 0,
    R is log(RA), !.


% ---------- DERIVADA EN UN PUNTO ----------

derivada_punto(Var, F, P, R) :-
    d(Var, F, Derivada),
    eval_expr(P, Derivada, R).


% ---------- INTEGRAL DEFINIDA ----------

area_definida(Var, F, A, B, R) :-
    s(Var, F, Primitiva),
    eval_expr(B, Primitiva, FB),
    eval_expr(A, Primitiva, FA),
    R is FB-FA.
