class MyName:
	def __init__(self, *args, **kwargs):
		print('initialized')

	def printStuff(self):
		print("Hello")
		print("I have reached here")

gui = MyName()
gui.printStuff()
