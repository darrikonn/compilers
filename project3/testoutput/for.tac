            VAR                                            i
            VAR                                            j
           GOTO                                         main
main:    ASSIGN              1                             j
         ASSIGN              0                             i
lab1:        GE              i             10           lab2
            VAR                                           t1
            ADD              i              j             t1
         ASSIGN             t1                             j
            ADD              i              1              i
           GOTO                                         lab1
lab2:    APARAM                                            i
           CALL        writeln                              
         APARAM                                            j
           CALL        writeln                              
         RETURN                                             
