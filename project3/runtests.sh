#!/bin/bash
./renew.sh

java -jar TacGenerator.jar testcode/code_and.decaf | sed 1,2d > darris_tests/and.tac
java -jar TacGenerator.jar testcode/code_fact.decaf | sed 1,2d > darris_tests/fact.tac
java -jar TacGenerator.jar testcode/code_for.decaf | sed 1,2d > darris_tests/for.tac
java -jar TacGenerator.jar testcode/code_func.decaf | sed 1,2d > darris_tests/func.tac
java -jar TacGenerator.jar testcode/code_if.decaf | sed 1,2d > darris_tests/if.tac
java -jar TacGenerator.jar Example1.decaf | sed 1,2d > darris_tests/ex1.tac
java -jar TacGenerator.jar Example2.decaf | sed 1,2d > darris_tests/ex2.tac
java -jar TacGenerator.jar Example3.decaf | sed 1,2d > darris_tests/ex3.tac
java -jar TacGenerator.jar Example_with_errors.decaf > darris_tests/errors.txt
java -jar TacGenerator.jar Example_without_errors.decaf | sed 1,2d > darris_tests/noerrors.tac

java -jar TAC_Interpreter/JTacInt.jar darris_tests/and.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/fact.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/for.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/func.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/if.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/ex1.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/ex2.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/ex3.tac
java -jar TAC_Interpreter/JTacInt.jar darris_tests/noerrors.tac
