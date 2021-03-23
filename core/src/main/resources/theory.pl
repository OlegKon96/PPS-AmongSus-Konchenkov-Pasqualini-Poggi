% color(+Color)
color("Red").
color("Yellow").
color("Green").
color("Cyan").
color("Blue").
color("Orange").
color("Pink").

% checkPoint(+X, +Y)
checkPoint(X, Y) :- X > 0, Y > 0, X < 72, Y < 50.

% fieldOfViewCrewmate(+N)
fieldOfViewCrewmate(6).
fieldOfViewImpostor(10).
fieldOfViewCrewmateSabotaged(3).

% player(+Color, +ID, +Username, +Point(X,Y), +FieldOfView)
playerCrewmate(C, ID, U, point(X,Y), FOV) :- color(C), checkPoint(X, Y), fieldOfViewCrewmate(FOV).
playerImpostor(C, ID, U, point(X,Y), FOV) :- color(C), checkPoint(X, Y), fieldOfViewImpostor(FOV).
playerCrewmateSabotaged(C, ID, U, point(X,Y), FOV) :- color(C), checkPoint(X, Y), fieldOfViewCrewmateSabotaged(FOV).

createPlayers([player(U, ID)|T], [playerCrewmate("Green", ID, U, point(0,0), 6)|T2]) :- createPlayers(T, T2).
createPlayers([], []).

% All possible direction that a player can take
% direction(+Movement)
direction("Up").
direction("Down").
direction("Left").
direction("Right").

%tile
tile("wall",point(X,Y)).
tile("floor",point(X,Y)).
tile("emergency",point(X,Y)).
tile("vent",point(X,Y)).
tile("other",point(X,Y)).
tile("boundary",point(X,Y)).
map([T|Ts]).

%get type of tile from position in map
%search(point(2,1), [tile("wall",point(2,2)),tile("boundary",point(1,2))],O) --> O/no
%search(point(2,2), [tile("wall",point(2,2)),tile("boundary",point(1,2))],O) --> O/tile("wall",point(2,2))
search(P, [tile(T,P)|_],tile(T,P)).
search(X, [_|Xs],O) :- search(X, Xs,O).

%checkCollisionGhost with boundary
checkCollisionGhost(tile("boundary",_)).

%moveGhost player
%moveGhost(point(1,1), [tile("wall",point(2,2)),tile("boundary",point(1,2))], "Up",O) --> O/point(1,1)
moveGhost(P,M,D,P) :- move(D,P,X), search(X,M,T), checkCollisionGhost(T),!.
moveGhost(P,M,D,X) :- move(D,P,X).

%checkCollisionAlive with wall and emergency
checkCollisionAlive(tile("wall",_)):- !.
checkCollisionAlive(tile("emergency",_)).

%moveAlive player
%moveAlive(point(1,1), [tile("wall",point(2,2)),tile("floor",point(1,2))], "Up",O) --> O/point(1,2)
moveAlive(P,M,D,P) :- move(D,P,X), search(X,M,T), checkCollisionAlive(T),!.
moveAlive(P,M,D,X) :- move(D,P,X).

% Check the movement of the player and return new position.
% move("Down", point(1,1), X). --> X/point(1,0)
move(D, point(X,Y), point(X, Y2)) :- direction(D) = direction("Up"), Y2 is Y + 1, !.
move(D, point(X,Y), point(X, Y2)) :- direction(D) = direction("Down"), Y2 is Y - 1, !.
move(D, point(X,Y), point(X2, Y)) :- direction(D) = direction("Left"), X2 is X - 1, !.
move(D, point(X,Y), point(X2, Y)) :- direction(D) = direction("Right"), X2 is X + 1.

% Calculate the power of the first term raised to the power of the second term.
% pow(1, 2, Y). --> Y/1
pow(X, Esp, Y) :- pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y) :- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y) :- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y) :- pow(X, Temp * X, Esp-1, Y).

% Take two points and calculate the distance.
% distance(point(1,1), point(2,3), X). --> X/
distance(point(X,Y), point(X2,Y2), Dist):- DeltaX is X2-X, DeltaY is Y2-Y, pow(DeltaX,2,PowX), pow(DeltaY,2,PowY), Dist is sqrt(PowX+PowY).

% Check the distance from 2 list of points and return all points that have a distance lower than the input max distance.
% checkDistanceListList([point(1,1),point(2,1)], [point(3,30)], 34, O). --> O/point(3,30)
checkDistanceListList([X|Xs], Y, D, [X,O|T]) :- checkDistancePointList(X, Y, D, O), checkDistanceListList(Xs, Y, D, T).
checkDistanceListList([], Y, D, []).

% Check the distance between a point and a list of point and return all points that have a distance lower than the input max distance.
% checkDistancePointList(point(1,1), [point(3,30), point(2,1), point(3,1)], 2, O). --> O/point(2,1), point(3,1)
checkDistancePointList(X, [Y|Ys], D, [Y|T]) :- distance(X, Y, R), R =< D, checkDistancePointList(X, Ys, D, T), !.
checkDistancePointList(X, [Y|Ys], D, T) :- distance(X, Y, R), R >= D, checkDistancePointList(X, Ys, D, T).
checkDistancePointList(X, [], D, []).

%canVent
canVent(X,Y) :- member(X,Y).

% lengthList(+List, -LengthList)
lengthList([], 0).
lengthList([(_,_)|T],X) :- lengthList(T,N), X is N+1.

% closestPoint returns the closest cartesian point contained in the list from the point passed in the first term.
%closestPoint(point(0,0), [point(7,2), point(5,3), point(2,2)], X). --> X/point(2,2)
closestPoint(Point, [H|T], Min):- closestPoint(Point, T, H, Min).

closestPoint(Point, [], Min, Min).
closestPoint(Point, [H|T], Temp, Min):- distance(Point, H, Dist), distance(Point, Temp, Dist2), Dist < Dist2, !, closestPoint(Point, T, H, Min).
closestPoint(Point, [H|T], Temp, Min):- closestPoint(Point, T, Temp, Min).