package com.tangent.util

import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.AutoDerivation

trait CirceSupport extends ErrorAccumulatingCirceSupport with AutoDerivation { //a trait is like an abstract class in Java

  //will place any special deserialisers here for case classes that can't be auto-derived

}
