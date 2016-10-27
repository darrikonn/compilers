               VAR                                            i
               VAR                                            j
              GOTO                                         main
   main:    ASSIGN              0                             i
            ASSIGN              6                             j
               VAR                                           t1
                LT              i              1           lab3
            ASSIGN              0                            t1
              GOTO                                         lab4
   lab3:    ASSIGN              1                            t1
   lab4:       VAR                                           t2
                GT              j              5           lab5
            ASSIGN              0                            t2
              GOTO                                         lab6
   lab5:    ASSIGN              1                            t2
   lab6:       VAR                                           t3
               AND             t1             t2             t3
                EQ             t3              0           lab2
               VAR                                           t4
               ADD              j              1             t4
            ASSIGN             t4                             j
              GOTO                                         lab1
   lab2:       VAR                                           t5
               SUB              i              1             t5
            ASSIGN             t5                             i
   lab1:    APARAM                                            i
              CALL        writeln                              
            APARAM                                            j
              CALL        writeln                              
            RETURN                                             
