               VAR                                            i
               VAR                                            j
              GOTO                                         main
   main:    ASSIGN              1                             j
            ASSIGN              0                             i
   lab1:       VAR                                           t1
                LT              i             10           lab3
            ASSIGN              0                            t1
              GOTO                                         lab4
   lab3:    ASSIGN              1                            t1
   lab4:        EQ             t1              0           lab2
               VAR                                           t2
               ADD              i              j             t2
            ASSIGN             t2                             j
   lab5:       ADD              i              1              i
              GOTO                                         lab1
   lab2:    APARAM                                            i
              CALL        writeln                              
            APARAM                                            j
              CALL        writeln                              
            RETURN                                             
