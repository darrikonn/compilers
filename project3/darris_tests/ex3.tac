            VAR                                            i
           GOTO                                         main
 bla:    FPARAM                                            i
         ASSIGN              1                             i
         APARAM                                            i
           CALL        writeln                              
         RETURN                                             
bla2:    APARAM                                            i
           CALL        writeln                              
         RETURN                                             
main:    ASSIGN              0                             i
         APARAM                                            i
           CALL        writeln                              
         APARAM                                            1
           CALL            bla                              
         ASSIGN              2                             i
           CALL           bla2                              
         RETURN                                             
