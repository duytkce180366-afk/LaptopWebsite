package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Category;
import java.util.*;

public class CategoryRepository {
    private static final List<Category> categories = new ArrayList<>();
    private static final Map<String, List<Map<String, String>>> secondaryFilterOptions = new HashMap<>();
    
    static {
        initializeCategories();
        initializeSecondaryFilters();
    }
    
    private static void initializeCategories() {
        // Laptops
        categories.add(new Category("laptops", "Laptops",
            Arrays.asList(
                createMenuGroup("Brands", "Dell", "ASUS", "Lenovo", "HP", "Acer"),
                createMenuGroup("Prices", "Under 20M", "20M - 30M", "30M - 40M", "Over 40M"),
                createMenuGroup("Purpose", "Gaming", "Workstation", "Student", "Business"),
                createMenuGroup("CPU", "Intel Core Ultra 7", "Intel Core i7", "AMD Ryzen 9"),
                createMenuGroup("GPU", "RTX 4070", "RTX 4060", "Integrated"),
                createMenuGroup("Screen", "13.6 inch", "14 inch QHD", "15.6 inch FHD", "16 inch OLED")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("cpu", "CPU"),
                createFilter("gpu", "GPU"),
                createFilter("ram", "RAM"),
                createFilter("storage", "Storage"),
                createFilter("display", "Display"),
                createFilter("battery", "Battery")
            )
        ));
        
        // Mouse
        categories.add(new Category("mouse", "Mouse",
            Arrays.asList(
                createMenuGroup("Brands", "Logitech", "Razer", "SteelSeries", "Corsair", "ASUS"),
                createMenuGroup("Prices", "Under 500K", "500K - 1M", "1M - 2M", "Over 2M"),
                createMenuGroup("Purpose", "Gaming", "Office", "Wireless", "Ergonomic"),
                createMenuGroup("Sensor", "Hero 25K", "Focus Pro", "TrueMove Air", "PixArt 3395"),
                createMenuGroup("Connection", "Bluetooth", "2.4GHz", "USB-C wired")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("sensor", "Sensor"),
                createFilter("dpi", "DPI"),
                createFilter("connection", "Connection"),
                createFilter("weight", "Weight")
            )
        ));
        
        // Keyboards
        categories.add(new Category("keyboards", "Keyboards",
            Arrays.asList(
                createMenuGroup("Brands", "Keychron", "Logitech", "Razer", "Akko", "Corsair"),
                createMenuGroup("Prices", "Under 1M", "1M - 2M", "2M - 3M", "Over 3M"),
                createMenuGroup("Purpose", "Gaming", "Office", "Wireless", "Compact"),
                createMenuGroup("Switch", "Red", "Brown", "Blue", "Optical"),
                createMenuGroup("Layout", "60%", "75%", "TKL", "Full-size")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("switchType", "Switch"),
                createFilter("layout", "Layout"),
                createFilter("connection", "Connection"),
                createFilter("backlight", "Backlight")
            )
        ));
        
        // Monitors
        categories.add(new Category("monitors", "Monitors",
            Arrays.asList(
                createMenuGroup("Brands", "LG", "Samsung", "Dell", "ASUS", "AOC"),
                createMenuGroup("Prices", "Under 4M", "4M - 7M", "7M - 12M", "Over 12M"),
                createMenuGroup("Purpose", "Gaming", "Design", "Office", "Ultrawide"),
                createMenuGroup("Resolution", "Full HD", "QHD", "4K UHD", "Ultrawide"),
                createMenuGroup("Refresh rate", "75Hz", "144Hz", "165Hz", "240Hz")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("size", "Size"),
                createFilter("resolution", "Resolution"),
                createFilter("refreshRate", "Refresh Rate"),
                createFilter("panel", "Panel")
            )
        ));
        
        // SSD
        categories.add(new Category("ssd", "SSD",
            Arrays.asList(
                createMenuGroup("Brands", "Samsung", "WD", "Kingston", "Crucial", "Seagate"),
                createMenuGroup("Prices", "Under 1M", "1M - 2M", "2M - 4M", "Over 4M"),
                createMenuGroup("Purpose", "Boot drive", "Gaming", "Creator", "NAS"),
                createMenuGroup("Capacity", "500GB", "1TB", "2TB", "4TB"),
                createMenuGroup("Interface", "SATA", "PCIe 3.0", "PCIe 4.0", "PCIe 5.0")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("capacity", "Capacity"),
                createFilter("interfaceType", "Interface"),
                createFilter("readSpeed", "Read Speed"),
                createFilter("formFactor", "Form Factor")
            )
        ));
        
        // RAM
        categories.add(new Category("ram", "RAM",
            Arrays.asList(
                createMenuGroup("Brands", "Corsair", "Kingston", "G.Skill", "Crucial", "TeamGroup"),
                createMenuGroup("Prices", "Under 1M", "1M - 2M", "2M - 3M", "Over 3M"),
                createMenuGroup("Purpose", "Laptop upgrade", "Gaming PC", "Workstation", "RGB build"),
                createMenuGroup("Capacity", "8GB", "16GB", "32GB", "64GB"),
                createMenuGroup("Type", "DDR3", "DDR4", "DDR5")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("capacity", "Capacity"),
                createFilter("memoryType", "Memory Type"),
                createFilter("bus", "Bus Speed"),
                createFilter("formFactor", "Form Factor")
            )
        ));
        
        // CPU
        categories.add(new Category("cpu", "CPU",
            Arrays.asList(
                createMenuGroup("Brands", "Intel", "AMD"),
                createMenuGroup("Prices", "Under 5M", "5M - 10M", "10M - 15M", "Over 15M"),
                createMenuGroup("Purpose", "Gaming", "Office", "Streaming", "Workstation"),
                createMenuGroup("Socket", "LGA1200", "LGA1700", "AM4", "AM5"),
                createMenuGroup("Cores", "6 cores", "8 cores", "12 cores", "16 cores")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("socket", "Socket"),
                createFilter("cores", "Cores"),
                createFilter("threads", "Threads"),
                createFilter("tdp", "TDP")
            )
        ));
        
        // GPU
        categories.add(new Category("gpu", "GPU",
            Arrays.asList(
                createMenuGroup("Brands", "ASUS", "MSI", "Gigabyte", "Sapphire", "Zotac"),
                createMenuGroup("Prices", "Under 10M", "10M - 20M", "20M - 35M", "Over 35M"),
                createMenuGroup("Purpose", "1080p gaming", "1440p gaming", "4K gaming", "AI work"),
                createMenuGroup("Chipset", "RTX 4060", "RTX 4070", "RTX 4080", "RTX 4090"),
                createMenuGroup("VRAM", "6GB", "8GB", "12GB", "16GB")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("chipset", "Chipset"),
                createFilter("vram", "VRAM"),
                createFilter("power", "Power"),
                createFilter("ports", "Ports")
            )
        ));
        
        // PC Case
        categories.add(new Category("pc-case", "PC Case",
            Arrays.asList(
                createMenuGroup("Brands", "NZXT", "Corsair", "Lian Li", "Cooler Master", "DeepCool"),
                createMenuGroup("Prices", "Under 2M", "2M - 3M", "3M - 5M", "Over 5M"),
                createMenuGroup("Purpose", "Compact", "Airflow", "Showcase", "Silent build"),
                createMenuGroup("Type", "Mini tower", "Mid tower", "Full tower"),
                createMenuGroup("Motherboard Support", "Mini-ITX", "Micro-ATX", "ATX", "E-ATX")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("caseType", "Case Type"),
                createFilter("motherboardSupport", "Motherboard Support"),
                createFilter("color", "Color"),
                createFilter("fanSupport", "Fan Support")
            )
        ));
        
        // Mainboard
        categories.add(new Category("mainboard", "Mainboard",
            Arrays.asList(
                createMenuGroup("Brands", "ASUS", "MSI", "Gigabyte", "ASRock", "Biostar"),
                createMenuGroup("Prices", "Under 3M", "3M - 5M", "5M - 8M", "Over 8M"),
                createMenuGroup("Purpose", "Budget PC", "Gaming", "Creator", "Overclocking"),
                createMenuGroup("Socket", "LGA1200", "LGA1700", "AM4", "AM5"),
                createMenuGroup("Chipset", "B660", "B760", "Z790", "B550", "B650")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("socket", "Socket"),
                createFilter("chipset", "Chipset"),
                createFilter("formFactor", "Form Factor"),
                createFilter("memoryType", "Memory Type")
            )
        ));
        
        // PC Fan
        categories.add(new Category("pc-fan", "PC Fan",
            Arrays.asList(
                createMenuGroup("Brands", "Noctua", "Corsair", "Cooler Master", "DeepCool", "Arctic"),
                createMenuGroup("Prices", "Under 300K", "300K - 700K", "700K - 1.2M", "Over 1.2M"),
                createMenuGroup("Purpose", "Silent", "RGB", "Radiator", "High airflow"),
                createMenuGroup("Size", "120mm", "140mm", "200mm"),
                createMenuGroup("Bearing", "Fluid dynamic", "Magnetic levitation", "SSO2")
            ),
            Arrays.asList(
                createFilter("brand", "Brand"),
                createFilter("purpose", "Purpose"),
                createFilter("size", "Size"),
                createFilter("speed", "Speed"),
                createFilter("airflow", "Airflow"),
                createFilter("lighting", "Lighting")
            )
        ));
    }
    
    private static Map<String, Object> createMenuGroup(String title, String... options) {
        Map<String, Object> group = new LinkedHashMap<>();
        group.put("title", title);
        group.put("options", Arrays.asList(options));
        return group;
    }
    
    private static Map<String, String> createFilter(String key, String label) {
        Map<String, String> filter = new HashMap<>();
        filter.put("key", key);
        filter.put("label", label);
        return filter;
    }
    
    private static void initializeSecondaryFilters() {
        // Laptops
        Map<String, String> laptopsFilters = new HashMap<>();
        laptopsFilters.put("brand", "Apple,Dell,ASUS,Lenovo,HP,Acer,MSI,Gigabyte,Microsoft Surface,LG");
        laptopsFilters.put("purpose", "Gaming,Workstation,Student,Business,Office,Creator,Thin and light,AI laptop");
        laptopsFilters.put("cpu", "Intel Core i3,Intel Core i5,Intel Core i7,Intel Core i9,Intel Core Ultra 5,Intel Core Ultra 7,Intel Core Ultra 9,AMD Ryzen 5,AMD Ryzen 7,AMD Ryzen 9,Apple M3,Apple M4");
        laptopsFilters.put("gpu", "Integrated,Intel Arc Graphics,RTX 3050,RTX 4050,RTX 4060,RTX 4070,RTX 4080,RTX 4090,Radeon 780M");
        laptopsFilters.put("ram", "8GB,16GB,24GB,32GB,64GB");
        laptopsFilters.put("storage", "256GB SSD,512GB SSD,1TB SSD,2TB SSD,4TB SSD");
        laptopsFilters.put("display", "13.3 inch FHD,13.6 inch Retina,14 inch QHD,14 inch 2.8K OLED,15.6 inch FHD,16 inch OLED,17.3 inch QHD");
        laptopsFilters.put("battery", "41Wh,52Wh,55Wh,65Wh,76Wh,90Wh,99Wh");
        secondaryFilterOptions.put("laptops", convertToListOfMaps(laptopsFilters));
        
        // Mouse
        Map<String, String> mouseFilters = new HashMap<>();
        mouseFilters.put("brand", "Logitech,Razer,SteelSeries,Corsair,ASUS,Glorious,Pulsar,Zowie");
        mouseFilters.put("purpose", "Gaming,Office,Wireless,Ergonomic,FPS,MOBA,Travel");
        mouseFilters.put("sensor", "Hero 25K,Focus Pro,TrueMove Air,PixArt 3395,PixArt 3370,ROG AimPoint");
        mouseFilters.put("dpi", "12000 DPI,16000 DPI,18000 DPI,26000 DPI,30000 DPI,32000 DPI,36000 DPI");
        mouseFilters.put("connection", "Bluetooth,2.4GHz,USB-C wired,Tri-mode");
        mouseFilters.put("weight", "Under 50g,50g - 60g,60g - 70g,70g - 90g,Over 90g");
        secondaryFilterOptions.put("mouse", convertToListOfMaps(mouseFilters));
        
        // Keyboards
        Map<String, String> keyboardsFilters = new HashMap<>();
        keyboardsFilters.put("brand", "Keychron,Logitech,Razer,Akko,Corsair,Ducky,Leopold,ASUS");
        keyboardsFilters.put("purpose", "Gaming,Office,Wireless,Compact,Creator,Typing");
        keyboardsFilters.put("switchType", "Red,Brown,Blue,Silver,Optical,Magnetic,Low profile");
        keyboardsFilters.put("layout", "60%,65%,75%,TKL,Full-size,Alice");
        keyboardsFilters.put("connection", "Bluetooth,2.4GHz,USB-C wired,Tri-mode");
        keyboardsFilters.put("backlight", "None,White,RGB,Per-key RGB");
        secondaryFilterOptions.put("keyboards", convertToListOfMaps(keyboardsFilters));
        
        // Monitors
        Map<String, String> monitorsFilters = new HashMap<>();
        monitorsFilters.put("brand", "LG,Samsung,Dell,ASUS,AOC,MSI,Gigabyte,BenQ");
        monitorsFilters.put("purpose", "Gaming,Design,Office,Ultrawide,Console,Programming");
        monitorsFilters.put("size", "24 inch,25 inch,27 inch,32 inch,34 inch,49 inch");
        monitorsFilters.put("resolution", "Full HD,QHD,4K UHD,5K,Ultrawide,Super Ultrawide");
        monitorsFilters.put("refreshRate", "60Hz,75Hz,100Hz,144Hz,165Hz,180Hz,240Hz,360Hz");
        monitorsFilters.put("panel", "IPS,Nano IPS,IPS Black,VA,OLED,QD-OLED,TN");
        secondaryFilterOptions.put("monitors", convertToListOfMaps(monitorsFilters));
        
        // SSD
        Map<String, String> ssdFilters = new HashMap<>();
        ssdFilters.put("brand", "Samsung,WD,Kingston,Crucial,Seagate,Lexar,ADATA,Corsair");
        ssdFilters.put("purpose", "Boot drive,Gaming,Creator,NAS,Portable,PS5 upgrade");
        ssdFilters.put("capacity", "250GB,500GB,1TB,2TB,4TB,8TB");
        ssdFilters.put("interfaceType", "SATA,PCIe 3.0,PCIe 4.0,PCIe 5.0,USB 3.2");
        ssdFilters.put("readSpeed", "560MB/s,3500MB/s,5000MB/s,7000MB/s,7450MB/s,10000MB/s,12000MB/s");
        ssdFilters.put("formFactor", "2.5 inch,M.2 2230,M.2 2242,M.2 2280,External");
        secondaryFilterOptions.put("ssd", convertToListOfMaps(ssdFilters));
        
        // RAM
        Map<String, String> ramFilters = new HashMap<>();
        ramFilters.put("brand", "Corsair,Kingston,G.Skill,Crucial,TeamGroup,ADATA,Patriot");
        ramFilters.put("purpose", "Laptop upgrade,Gaming PC,Workstation,RGB build,Office PC");
        ramFilters.put("capacity", "8GB,16GB,32GB,48GB,64GB,96GB,128GB");
        ramFilters.put("memoryType", "DDR3,DDR4,DDR5,LPDDR5,SODIMM");
        ramFilters.put("bus", "2666MHz,3200MHz,3600MHz,4800MHz,5200MHz,5600MHz,6000MHz,6400MHz,7200MHz");
        ramFilters.put("formFactor", "DIMM,SODIMM,CAMM2");
        secondaryFilterOptions.put("ram", convertToListOfMaps(ramFilters));
        
        // CPU
        Map<String, String> cpuFilters = new HashMap<>();
        cpuFilters.put("brand", "Intel,AMD");
        cpuFilters.put("purpose", "Gaming,Office,Streaming,Workstation,Budget PC,Creator");
        cpuFilters.put("socket", "LGA1200,LGA1700,LGA1851,AM4,AM5,sTRX4");
        cpuFilters.put("cores", "4 cores,6 cores,8 cores,10 cores,12 cores,14 cores,16 cores,24 cores,32 cores");
        cpuFilters.put("threads", "8 threads,12 threads,16 threads,20 threads,24 threads,28 threads,32 threads,64 threads");
        cpuFilters.put("tdp", "35W,65W,95W,105W,120W,125W,170W,280W");
        secondaryFilterOptions.put("cpu", convertToListOfMaps(cpuFilters));
        
        // GPU
        Map<String, String> gpuFilters = new HashMap<>();
        gpuFilters.put("brand", "ASUS,MSI,Gigabyte,Sapphire,Zotac,PNY,PowerColor,Galax");
        gpuFilters.put("purpose", "1080p gaming,1440p gaming,4K gaming,AI work,Streaming,Creator");
        gpuFilters.put("chipset", "RTX 3050,RTX 4060,RTX 4060 Ti,RTX 4070,RTX 4070 Super,RTX 4080,RTX 4090,RX 7600,RX 7700 XT,RX 7800 XT,RX 7900 XTX");
        gpuFilters.put("vram", "6GB,8GB,10GB,12GB,16GB,20GB,24GB");
        gpuFilters.put("power", "115W,160W,200W,220W,263W,320W,355W,450W");
        gpuFilters.put("ports", "HDMI, DisplayPort,HDMI, 3x DisplayPort,2x HDMI, 2x DisplayPort,USB-C, HDMI, DisplayPort");
        secondaryFilterOptions.put("gpu", convertToListOfMaps(gpuFilters));
        
        // PC Case
        Map<String, String> pcCaseFilters = new HashMap<>();
        pcCaseFilters.put("brand", "NZXT,Corsair,Lian Li,Cooler Master,DeepCool,Fractal Design,Phanteks");
        pcCaseFilters.put("purpose", "Compact,Airflow,Showcase,Silent build,Water cooling,Budget build");
        pcCaseFilters.put("caseType", "Mini tower,Mid tower,Full tower,Open frame");
        pcCaseFilters.put("motherboardSupport", "Mini-ITX,Micro-ATX,ATX,E-ATX");
        pcCaseFilters.put("color", "Black,White,Gray,Silver");
        pcCaseFilters.put("fanSupport", "3 fans,5 fans,6 fans,8 fans,9 fans,10 fans,12 fans");
        secondaryFilterOptions.put("pc-case", convertToListOfMaps(pcCaseFilters));
        
        // Mainboard
        Map<String, String> mainboardFilters = new HashMap<>();
        mainboardFilters.put("brand", "ASUS,MSI,Gigabyte,ASRock,Biostar,NZXT");
        mainboardFilters.put("purpose", "Budget PC,Gaming,Creator,Overclocking,Workstation,Small form factor");
        mainboardFilters.put("socket", "LGA1200,LGA1700,LGA1851,AM4,AM5");
        mainboardFilters.put("chipset", "H610,B660,B760,Z790,A520,B550,X570,A620,B650,X670");
        mainboardFilters.put("formFactor", "Mini-ITX,Micro-ATX,ATX,E-ATX");
        mainboardFilters.put("memoryType", "DDR4,DDR5");
        secondaryFilterOptions.put("mainboard", convertToListOfMaps(mainboardFilters));
        
        // PC Fan
        Map<String, String> pcFanFilters = new HashMap<>();
        pcFanFilters.put("brand", "Noctua,Corsair,Cooler Master,DeepCool,Arctic,Lian Li,Thermaltake");
        pcFanFilters.put("purpose", "Silent,RGB,Radiator,High airflow,Static pressure,Budget cooling");
        pcFanFilters.put("size", "80mm,92mm,120mm,140mm,200mm");
        pcFanFilters.put("speed", "1200 RPM,1500 RPM,1700 RPM,1800 RPM,1850 RPM,2000 RPM,2100 RPM,3000 RPM");
        pcFanFilters.put("airflow", "40 CFM,50 CFM,60 CFM,62 CFM,65 CFM,68 CFM,72 CFM,90 CFM");
        pcFanFilters.put("lighting", "None,White LED,RGB,ARGB");
        secondaryFilterOptions.put("pc-fan", convertToListOfMaps(pcFanFilters));
    }
    
    private static List<Map<String, String>> convertToListOfMaps(Map<String, String> filterMap) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            Map<String, String> item = new HashMap<>();
            item.put("key", entry.getKey());
            item.put("label", entry.getKey());
            item.put("values", entry.getValue());
            result.add(item);
        }
        return result;
    }
    
    public static List<Category> getAll() {
        return new ArrayList<>(categories);
    }
    
    public static Category getById(String id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public static Map<String, List<Map<String, String>>> getSecondaryFilterOptions() {
        return new HashMap<>(secondaryFilterOptions);
    }
    
    public static List<Map<String, String>> getSecondaryFilterOptionsByCategory(String categoryId) {
        return secondaryFilterOptions.getOrDefault(categoryId, new ArrayList<>());
    }
}
