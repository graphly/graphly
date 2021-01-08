package model

import util.GraphlyTest

class LinearTransformTests extends GraphlyTest {
  var identityRotation = LinearTransform.radians(2 * math.Pi)
  var identity = LinearTransform(1, 0, 0, 1)
  var mapToZero = LinearTransform(0, 0, 0, 0)

  var point = Position(3, 2)
  var pointClose = Position(3.1, 2.1)
  var pointZero = Position(0, 0)
  var pointZeroClose = Position(0.1, 0.1)

  it should "be in the same position after rotation" in {
    // `fuzzy=0.01` is a hack to avoid floating point error
    assert(identityRotation.apply(point).inRectangle(point, pointClose, fuzzy=0.01))
  }

  it should "be in the same position after identity" in {
    assert(identity.apply(point).inRectangle(point, pointClose, fuzzy=0.01))
  }

  it should "be in at zero" in {
    assert(mapToZero.apply(point).inRectangle(pointZero, pointZeroClose, fuzzy=0.01))
  }
}
