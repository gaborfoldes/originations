
require "./LOC"
require "date"

include LOC

l = LOC::Ledger.new
l.entries.push LedgerEntry.new(LedgerEntry::Fee, "Monthly fee", Date.new(2013,2,2), 6.25, LedgerEntry::Credit)
l.entries.push LedgerEntry.new(LedgerEntry::Principal, "Transfer", Date.new(2013,2,1), 200, LedgerEntry::Credit)
puts l
puts l.balance(LedgerEntry::Principal, nil)
puts l.balance(LedgerEntry::Total, nil)
puts l.outstanding_last_draw(nil)