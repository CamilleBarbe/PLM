# BEGIN TEMPLATE
def lastDigit(a, b, c):
	# BEGIN SOLUTION
	da = a % 10
	db = b % 10
	dc = c % 10
	return da == db or da == dc or dc == db

# END SOLUTION
# END TEMPLATE
