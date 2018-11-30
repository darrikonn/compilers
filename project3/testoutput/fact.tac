            VAR                                            j
            VAR                                            n
           GOTO                                         main
fact:    FPARAM                                            n
            VAR                                            k
             GE              n              1           lab1
         ASSIGN              1                          fact
           GOTO                                         lab2
lab1:       VAR                                           t1
            SUB              n              1             t1
         APARAM                                           t1
           CALL           fact                              
         ASSIGN           fact                             k
            VAR                                           t2
           MULT              n              k             t2
         ASSIGN             t2                          fact
lab2:    RETURN                                             
main:    ASSIGN              5                             j
lab3:        LE              j              0           lab4
         APARAM                                            j
           CALL           fact                              
         ASSIGN           fact                             n
         APARAM                                            n
           CALL        writeln                              
            SUB              j              1              j
           GOTO                                         lab3
lab4:    RETURN                                             
