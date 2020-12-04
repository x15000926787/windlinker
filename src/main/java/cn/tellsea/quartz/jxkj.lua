local function Sleep(n)
  -- if n > 0 then os.execute("ping -n " .. tonumber(n + 1) .. " localhost > NUL") end
 -- redis.
end
local n1 = tonumber(redis.call('get', 'ai_0'))
local n2 = tonumber(redis.call('get', 'ai_1'))
local v1 ,v2,v3
      v1 = n1 and n2
      v2 = n1 or n2
      v3 = not n2
local a=redis.call('TIME') ;
--return a[1]*1000000+a[2] 
redis.call('set','dd',tostring(a[1]*1000000+a[2]));
--redis.call('ZADD', 'delay_job', a[1]*1000+math.floor(a[2]/1000)+5000, 'ch_1_.do_0_:1,ch_1_.do_1_:0')
local function max(num1, num2)
   local result
   if (num1 > num2) then
      result = num1;
   else
      result = num2;
   end

   return result; 
end

local function maximum (a)
    local mi = 1             -- 最大值索引
    local m = a[mi]          -- 最大值
    for i,val in ipairs(a) do
       if val > m then
           mi = i
           m = val
       end
    end
    return m, mi
end


local b={8,10,23,12,44}

  -- n1 = max(n1,n2)
  local n3,n4 = maximum(b)
   return a[1]*1000+math.floor(a[2]/1000)+5000

