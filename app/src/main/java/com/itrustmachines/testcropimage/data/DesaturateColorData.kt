package com.itrustmachines.testcropimage.data

class DesaturateColorData : ColorData(
    filterType = ColorType.ADJUST,
    hue = 0,
    saturation = -100,
    brightness = 0,
    contrast = 0,
    opacity = 100
)