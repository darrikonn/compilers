            VAR                                            i
            VAR                                            j
           GOTO                                         main
main:    ASSIGN              0                             i
         ASSIGN              6                             j
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
            VAR                                           t4
            ADD              j              1             t4
         ASSIGN             t4                             j
           GOTO                                         lab7
lab6:       VAR                                           t5
            SUB              i              1             t5
         ASSIGN             t5                             i
lab7:    APARAM                                            i
           CALL        writeln                              
         APARAM                                            j
           CALL        writeln                              
         RETURN                                             
