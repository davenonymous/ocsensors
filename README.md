# ocsensors
Sensor Addon for OpenComputers

Similiar to the old OpenCCSensors mod this mod adds a sensor block that allows
computers to scan surrounding blocks.

## Available Methods
| Method                                                                                | Description                                                      |
| ------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| scan(x:number, y:number, z:number, [side:number])                                     | Scans a block relative to the sensor                             |
| search([name:string=""], [meta:number=-1], [section:string=""], [range:number=<max>]) | Search for blocks matching the given criteria in the given range |
| searchEntities(x1:number, y1:number, z1:number, x2:number, y2:number, z3:number)      | Scans a region relative to the sensor for entities               |

## Examples

### Get stored energy of all energy-capable nearby blocks
```lua
local component = require('component')

local sensor = component.sensor

local totalCapacity = 0
local totalStored = 0

print("Searching for blocks handling energy")
local positions = sensor.search("", -1, "energy")

print("Scanning " .. #positions .. " blocks")
for _,pos in ipairs(positions) do
  local info = sensor.scan(pos.x, pos.y, pos.z)

  print("  " .. info.block.label .. " @ " .. pos.x .. "," .. pos.y .. "," .. pos.z)
  totalStored = totalStored + info.data.energy.energyStored
  totalCapacity = totalCapacity + info.data.energy.maxEnergyStored
end

print("")
print("Energy summary:")
print("  Total capacity: " .. totalCapacity);
print("  Total stored:   " .. totalStored);
```

### Output a redstone signal when all nearby extra utilities 2 generators are ready to run
```lua
local component = require("component")
local sides = require("sides")

local sensor = component.sensor
local redstone = component.redstone

local redstoneSide = sides.back

-- Search for generators once
local generators = sensor.search("", -1, "extrautils2")

while(true) do
    -- Loop over nearby machines and see if they are all satisfied
    local missing = 0
    for _,pos in ipairs(generators) do
        local info = sensor.scan(pos.x, pos.y, pos.z)
        local xu = info.data.extrautils2

        -- Is the generator already running?
        local alreadyRunning = info.data.extrautils2.processTime > 0

        -- Is the Generator satisfied with items?
        local enoughItems = info.data.items.n == 0 or info.data.items.n == #info.data.items

        -- Is it satisfied with fluid?
        local enoughFluid = true
        if(info.data.fluid.n > 0) then
            for _,tank in ipairs(info.data.fluid) do
                if(tank.contents == nil or tank.contents.amount == 0) then
                    enoughFluid = false
                end
            end
        end

        if(alreadyRunning or (enoughItems and enoughFluid)) then
            --print(info.block.label .. ": " .. serialization.serialize(info.data))
            print("Ready:   " .. info.block.label)
        else
            print("Missing: " .. info.block.label)

            -- Immediately turn off all other generators
            redstone.setOutput(redstoneSide, 0)
            missing = missing + 1
        end
    end

    -- React accordingly
    if(missing == 0) then
        print("--> All generators good to go")
        redstone.setOutput(redstoneSide, 15)
    else
        print("--> Missing " .. missing .. " generators")
    end

    os.sleep(1)
end
```

### Dump the fluid of all nearby MooFluid cows
```lua
local component = require('component')
local serialization = require('serialization')

local sensor = component.sensor

local range = 5

local foundFluids = {}
for _, entity in ipairs(sensor.searchEntities(-range, -range, -range, range, range, range)) do
    if(entity.type == "neutral" and entity.moofluids ~= nil) then
        foundFluids[#foundFluids+1] = entity.moofluids.fluid.name
    end
end

print(serialization.serialize(foundFluids))
-- {"lava", "lava", "lava", "water", "water", "lava"}
```