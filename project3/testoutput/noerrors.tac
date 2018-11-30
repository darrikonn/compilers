            GOTO                                         main
 main:    FPARAM                                            i
          FPARAM                                            j
          ASSIGN              5                             i
          ASSIGN              5                             j
              NE              i              5           lab1
             VAR                                           t1
          ASSIGN              1                            t1
            GOTO                                         lab2
 lab1:       VAR                                           t1
          ASSIGN              0                            t1
 lab2:        NE              j              5           lab3
             VAR                                           t2
          ASSIGN              1                            t2
            GOTO                                         lab4
 lab3:       VAR                                           t2
          ASSIGN              0                            t2
 lab4:       VAR                                           t3
              OR             t1             t2             t3
              NE             t3              0           lab5
          APARAM                                            1
            CALL        writeln                              
 lab5:        LE              i              5           lab6
             VAR                                           t4
          ASSIGN              1                            t4
            GOTO                                         lab7
 lab6:       VAR                                           t4
          ASSIGN              0                            t4
 lab7:        NE              j              5           lab8
             VAR                                           t5
          ASSIGN              1                            t5
            GOTO                                         lab9
 lab8:       VAR                                           t5
          ASSIGN              0                            t5
 lab9:       VAR                                           t6
              OR             t4             t5             t6
              NE             t6              0          lab10
          APARAM                                            2
            CALL        writeln                              
lab10:        NE              i              5          lab11
             VAR                                           t7
          ASSIGN              1                            t7
            GOTO                                        lab12
lab11:       VAR                                           t7
          ASSIGN              0                            t7
lab12:        LE              j              5          lab13
             VAR                                           t8
          ASSIGN              1                            t8
            GOTO                                        lab14
lab13:       VAR                                           t8
          ASSIGN              0                            t8
lab14:       VAR                                           t9
              OR             t7             t8             t9
              NE             t9              0          lab15
          APARAM                                            3
            CALL        writeln                              
lab15:        LE              i              5          lab16
             VAR                                          t10
          ASSIGN              1                           t10
            GOTO                                        lab17
lab16:       VAR                                          t10
          ASSIGN              0                           t10
lab17:        GE              j              5          lab18
             VAR                                          t11
          ASSIGN              1                           t11
            GOTO                                        lab19
lab18:       VAR                                          t11
          ASSIGN              0                           t11
lab19:       VAR                                          t12
              OR            t10            t11            t12
              NE            t12              0          lab20
          APARAM                                         1337
            CALL        writeln                              
lab20:    RETURN                                             
