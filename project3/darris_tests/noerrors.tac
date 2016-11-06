           GOTO                                         main
main:    FPARAM                                            i
         FPARAM                                            j
         ASSIGN              5                             i
         ASSIGN              5                             j
             GE              i              5           lab1
            VAR                                           t1
         ASSIGN              1                            t1
           GOTO                                         lab2
lab1:    ASSIGN              0                            t1
lab2:        LE              j              5           lab3
            VAR                                           t2
         ASSIGN              1                            t2
           GOTO                                         lab4
lab3:    ASSIGN              0                            t2
lab4:       VAR                                           t3
            AND             t1             t2             t3
             EQ             t3              0           lab5
         APARAM                                         1337
           CALL        writeln                              
           GOTO                                         lab6
lab5:    APARAM                                            0
           CALL        writeln                              
lab6:    RETURN                                             
