<?php

class A {
  function f() {
    if (false) {        // OK
    }

    if (false) {        // OK
    } else {
    }

    if (false) {        // OK
      if (false) {      // NOK
      }
    }

    if (false) {        // OK
      if (false) {      // OK
      }
      doSomething();
    }

    if (false) {        // OK
      $a = 1;
      if (a) {          // OK
      }
    }

    if (false) {        // OK
      if (false) {      // OK
      }
    } else {
    }

    if (false) {        // OK
      if (false) {      // OK
      } else {
      }
    }

    if (false) {        // OK
    } else if (false) { // OK
      if (false) {      // NOK
      }
    }

    if (false)          // OK
      if (true) {       // NOK
      }

    if (true)           // OK
     doSomething();

    if (false) {        // OK
      while (true) {
        if (true) {     // OK
        }
      }
    }

    while (true)
      if(true) {        // OK
      }

    if (false) {        // OK
    }

    if (false) {        // OK
      doSomething();
    }

    if (true):
      if (false):       // NOK
        doSomething();
      endif;
    endif;

    if (true) {         // OK
      __halt_compiler();
    }
  }
}
