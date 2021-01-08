package model

import util.GraphlyTest

class LinearTransformTests extends GraphlyTest {
  var identityRotation = LinearTransform.radians(2 * math.Pi)
  var identity = LinearTransform(1, 0, 1, 0)
  var mapToZero = LinearTransform(0, 0, 0, 0)

  var point = Position(1, 1)

  it should "be in the same position after rotation" in {
    assert(identityRotation.apply(point).inRectangle(point, point))
  }

  it should "be in the same position after identity" in {
    assert(identity.apply(point).inRectangle(point, point))
  }

  it should "be in at zero" in {
    assert(mapToZero.apply(point).inRectangle(point, point))
  }
}
