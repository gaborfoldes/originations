
require "./LOC"
include LOC
require "date"

lines = Lines.new
lines.load_lines("/Users/gabor/Code/lines.tsv")
lines.load_draws("/Users/gabor/Code/draws.tsv")
lines.load_payments("/Users/gabor/Code/payments.tsv")
lines.print(2911)
puts lines.lines[2911].ledger


=begin
loc = LineOfCredit.new(Date.new(2013,1,11), 9999, "APP12345", 0, "gabor@billfloat.com", 750, Date.new(2013,2,15))
loc.draw(Date.new(2013,1,11), 200)
loc.move_forward_to(Date.new(2013,2,5))
loc.pay(Date.new(2013,2,15), 25)
puts loc.ledger
puts loc
=end