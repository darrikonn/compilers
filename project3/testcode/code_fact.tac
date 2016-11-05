               VAR                                            j
               VAR                                            n
              GOTO                                         main
   fact:    FPARAM                                            n
               VAR                                            k
               VAR                                           t1
                LT              n              1           lab3
            ASSIGN              0                            t1
              GOTO                                         lab4
   lab3:    ASSIGN              1                            t1
   lab4:        EQ             t1              0           lab2
            ASSIGN              1                          fact
              GOTO                                         lab1
   lab2:       VAR                                           t2
               SUB              n              1             t2
            APARAM                                           t2
              CALL           fact                              
            ASSIGN           fact                             k
               VAR                                           t3
              MULT              n              k             t3
            ASSIGN             t3                          fact
   lab1:    RETURN                                             
   main:    ASSIGN              5                             j
   lab5:       VAR                                           t4
                GT              j              0           lab7
            ASSIGN              0                            t4
              GOTO                                         lab8
   lab7:    ASSIGN              1                            t4
   lab8:        EQ             t4              0           lab6
            APARAM                                            j
              CALL           fact                              
            ASSIGN           fact                             n
            APARAM                                            n
              CALL        writeln                              
   lab9:       SUB              j              1              j
              GOTO                                         lab5
   lab6:    RETURN                                             
