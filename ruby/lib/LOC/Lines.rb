
require "date"

module LOC

	class Lines

		SEPARATOR = "\t"

		attr_accessor :lines	
	
		def initialize
			@lines = {}
			@exclusions = {}
		end
		
		def put(line) 
			@lines[line.id] = line
		end


		# display info
		def print(id=nil)
			puts LineOfCredit::HEADER + "\n"
			if id.nil? then
				@lines.each do |id, line| 
					puts line.to_s + "\n"
				end
			else
				puts lines[id].to_s + "\n"
			end
		end
		
		
		# load data
		def load_lines(file_name) 
			File.open(file_name, "r").each_line do |line|
				s = line.strip.split(SEPARATOR)
				s[0] = Date.parse(s[0])
				s[1] = s[1].to_i
				s[3] = s[3].to_i
				s[5] = s[5].to_f
				s[6] = Date.parse(s[6])
				put(LineOfCredit.new(*s))
			end
		end

		def load_draws(file_name) 
			File.open(file_name, "r").each_line do |line|
				s = line.strip.split(SEPARATOR)
				l = @lines[s[0].to_i]
				l.draw(Date.parse(s[1]), s[2].to_f) if !l.nil?
			end
		end
			
		def load_payments(file_name) 
			File.open(file_name, "r").each_line do |line|
				s = line.strip.split(SEPARATOR)
				l = @lines[s[0].to_i]
				l.pay(Date.parse(s[1]), s[2].to_f) if !l.nil?
			end
		end			


=begin	
		
		/* get all lines in sync */
		def moveForwardTo(Date date) 
			for(LineOfCredit line : lines.values()) 
				line.moveForwardTo(date);
			end
		end
	
		/* queries */
		public List<LineOfCredit> getLinesDue(Date date) 
			List<LineOfCredit> linesDue = new ArrayList<LineOfCredit>();
			for(LineOfCredit line : lines.values()) 
				if (line.getNextDueDate().equals(date)) linesDue.add(line);
			end
			return linesDue;
		end
		
		public LineOfCredit getByEmail(String email) 
			LineOfCredit lineByEmail = null;
			for(LineOfCredit line : lines.values()) 
				if (line.getEmail().toLowerCase().equals(email.toLowerCase())) 
					lineByEmail = line;
					break;
				end
			end
			return lineByEmail;
		end
		
		public LineOfCredit getByAppNumber(String appNumber) 
			LineOfCredit lineByAppNumber = null;
			for(LineOfCredit line : lines.values()) 
				if (line.getAppNumber().toLowerCase().equals(appNumber.toLowerCase())) 
					lineByAppNumber = line;
					break;
				end
			end
			return lineByAppNumber;
		end
=end

	end		
		
end
	