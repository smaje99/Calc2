package com.calc2.domain;

/** Unit used to interpret trigonometric function arguments such as {@code sin(90)}. */
public enum AngleUnit {
    /** A full turn contains 360 units. */
    DEGREES,
    /** A full turn contains {@code 2 * pi} units. */
    RADIANS
}
