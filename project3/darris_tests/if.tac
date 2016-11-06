           GOTO                                         main
main:       VAR                                            i
            VAR                                            j
         ASSIGN              1                             i
            VAR                                           t1
         UMINUS              1                            t1
         ASSIGN             t1                             j
             LE              i              0           lab1
            VAR                                           t2
            VAR                                           t3
           MULT              j              3             t3
            SUB              1             t3             t2
         ASSIGN             t2                             i
           GOTO                                         lab2
lab2:    APARAM                                            i
           CALL        writeln                              
         RETURN                                             
