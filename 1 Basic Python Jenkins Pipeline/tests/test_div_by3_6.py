import pytest

@pytest.mark.divisable
def test_divisible_by_3(input_value):
    assert input_value %3 == 0

@pytest.mark.divisable
def test_divisible_by_6(input_value):
    assert input_value %6 == 0

