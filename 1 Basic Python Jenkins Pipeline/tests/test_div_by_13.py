import pytest

@pytest.mark.divisable
def test_divisable_by_13(input_value):
    assert input % 13 == 0
