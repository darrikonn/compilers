                VAR                                            x
                VAR                                            y
               GOTO                                         main
     add:    FPARAM                                            x
             FPARAM                                            y
                VAR                                           t1
                ADD              x              y             t1
             ASSIGN             t1                           add
             RETURN                                             
    mult:    FPARAM                                            x
             FPARAM                                            y
                VAR                                           t2
               MULT              x              y             t2
             ASSIGN             t2                          mult
             RETURN                                             
toScreen:    FPARAM                                            x
                VAR                                            y
             ASSIGN              x                             y
             APARAM                                            y
               CALL        writeln                              
             RETURN                                             
    main:    ASSIGN              3                             x
             APARAM                                            x
               CALL       toScreen                              
             APARAM                                            x
             APARAM                                            2
               CALL            add                              
             ASSIGN            add                             x
             APARAM                                            x
               CALL       toScreen                              
             APARAM                                            x
             APARAM                                            3
               CALL           mult                              
             ASSIGN           mult                             x
             APARAM                                            x
               CALL       toScreen                              
             RETURN                                             
