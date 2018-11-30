            VAR                                            i
           GOTO                                         main
proc:    FPARAM                                            a
         FPARAM                                            b
            VAR                                            x
            VAR                                           t1
            SUB              a              b             t1
         ASSIGN             t1                             x
         ASSIGN              x                          proc
         RETURN                                             
main:       VAR                                           t2
            VAR                                           t3
           MULT              2              3             t3
            ADD              1             t3             t2
            VAR                                           t4
            VAR                                           t5
           MULT              2              5             t5
         UMINUS             t5                            t4
         APARAM                                           t2
         APARAM                                           t4
           CALL           proc                              
         ASSIGN           proc                             i
         APARAM                                            i
           CALL        writeln                              
         RETURN                                             
