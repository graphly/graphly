package model

import util.GraphlyTest
import ui.Position

class PositionTests extends GraphlyTest {
    var posA = Position(10, 10)
    var posB = Position(20, 20)

    it should "be inside rectangle (basic testcase)" in {
        assert(Position(15, 15).inRectangle(posA, posB))
    }

    it should "be outside rectangle (edge testcase)" in {
        assert(!Position(10, 20).inRectangle(posA, posB))
    }

    it should "be outside rectangle 1" in {
        assert(!Position(15, 25).inRectangle(posA, posB))
    }

    it should "be outside rectangle 2" in {
        assert(!Position(25, 15).inRectangle(posA, posB))
    }

    it should "be outside rectangle 3" in {
        assert(!Position(25, 25).inRectangle(posA, posB))
    }
}

