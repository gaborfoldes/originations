module LOC

	class Ledger

		attr_accessor :entries

		def initialize
			@entries = []
		end
		
		def balance(type, date)
			s = 0
			@entries.each do |entry|
				if (date == nil || entry.postDate <= date)
					s += (entry.type == type || type == LOC::LedgerEntry::Total ? entry.signed_amount : 0)
				end
			end
			return s
		end
		
		def outstanding_last_draw(date)
			@entries.sort! { |a,b| a.post_date <=> b.post_date }
			last_draw = 0;
			s = 0;
			@entries.each do |entry|
				if (date == nil || entry.postDate <= date)
					if (entry.type == LOC::LedgerEntry::Principal)
						s += entry.signed_amount
						if (entry.signed_amount > 0)
							last_draw = s
						end
					end
				end
			end
			return last_draw
		end
		
		def to_s
			@entries.sort! { |a,b| a.post_date <=> b.post_date }
			all = LOC::LedgerEntry::Header + "\n"
			@entries.each do |entry|
				all += entry.to_s + "\n"
			end
			return all
		end
	
	end

end