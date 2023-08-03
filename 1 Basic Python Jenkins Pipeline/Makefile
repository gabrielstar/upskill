.PHONE venv:
	python3 -m venv venv
	./venv/bin/python3 -m pip install --upgrade pip

.PHONY: setup
setup: venv
	./venv/bin/pip3 install -r requirements.txt

.PHONY:  run_test
run_test: setup
	./venv/bin/python3 ./tests/dumy_test.py

