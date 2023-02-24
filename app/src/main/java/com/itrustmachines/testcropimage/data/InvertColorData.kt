package com.itrustmachines.testcropimage.data

class InvertColorData : ColorData(
    filterType = ColorType.ADJUST,
    hue = -100,
    saturation = 0,
    brightness = 0,
    contrast = 0,
    opacity = 100
)