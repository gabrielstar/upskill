import pytest

'''Pytest will execute the xfailed test, but it will not be considered as part failed or passed tests. 
Details of these tests will not be printed even if the test fails (remember pytest usually prints the failed test details). 
We can xfail tests using the following marker −
@pytest.mark.xfail

Skipping a test means that the test will not be executed. We can skip tests using the following marker −
@pytest.mark.skip
'''
@pytest.mark.xfail
@pytest.mark.great
def test_greater():
    num = 100
    assert num > 100

@pytest.mark.xfail
@pytest.mark.great
def test_greater_equal():
    num = 100
    assert num >=100

@pytest.mark.skip
@pytest.mark.others
def test_less():
    num = 100
    assert num < 200
