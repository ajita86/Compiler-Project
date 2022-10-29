PROGRAM parms1;

VAR
    i, j : integer;
    x, y : real;

PROCEDURE goodSwap(VAR v1, v2 : integer);
    VAR
        t : integer;

    BEGIN
        t := v1;
        v1 := v2;
        v2 := t;
    END;

PROCEDURE badSwap(v1, v2 : real);
    VAR
        t : real;

    BEGIN
        t := v1;
        v1 := v2;
        v2 := t;
    END;

BEGIN
    i := 10;
    j := 20;
    writeln('Calling goodSwap: i = ', i, ', j = ', j);
    goodSwap(i, j);
    writeln('          Result: i = ', i, ', j = ', j);

    x := 10;
    y := 20;
    writeln('Calling  badSwap: x = ', x:5:1, ', y = ', y:5:1);
    badSwap(x, y);
    writeln('          Result: x = ', x:5:1, ', y = ', y:5:1);
END.
