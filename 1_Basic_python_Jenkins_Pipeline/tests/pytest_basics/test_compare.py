import pytest

@pytest.mark.greater
def test_greater():
    num = 100
    assert num > 100

@pytest.mark.greater
def test_greater_equal():
    num = 100
    assert num >= 100

@pytest.mark.others
def test_less():
    num = 100
    assert num < 200

@pytest.mark.equal
def test_equal():
    num = 1
    assert num == 1

