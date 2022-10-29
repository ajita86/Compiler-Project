PROGRAM parms4;

VAR i : integer;

PROCEDURE proc(VAR v : integer);

    BEGIN
        v := 99;
        writeln('proc: i = ', i, ', v = ', v);
    END;

BEGIN
    i := 10;
    writeln('main: i = ', i);

    proc(i);
    writeln('main: i = ', i);
END.
