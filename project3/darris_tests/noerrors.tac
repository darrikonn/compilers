           GOTO                                         main
main:    FPARAM                                            i
         FPARAM                                            j
         ASSIGN              5                             i
         ASSIGN              5                             j
             GE              i              1           lab1
            VAR                                           t1
         ASSIGN              1                            t1
           GOTO                                         lab3
lab2:    ASSIGN              0                            t1
lab3:        LE              j              5           lab4
            VAR                                           t2
         ASSIGN              1                            t2
           GOTO                                         lab5
lab4:    ASSIGN              0                            t2
lab5:       VAR                                           t3
            AND             t1             t2             t3
             EQ             t3              0           lab6
         ASSIGN              0                             i
lab6:    RETURN                                             
