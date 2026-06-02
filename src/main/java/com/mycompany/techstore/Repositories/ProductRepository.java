package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;
import java.util.*;
import java.util.stream.Collectors;

public class ProductRepository {

    private static final List<Product> products = new ArrayList<>();
    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();

    static {
        PRODUCT_IMAGES.put("laptops", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("mouse", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("keyboards", "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("monitors", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("ssd", "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("ram", "https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("cpu", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("gpu", "https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("pc-case", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("mainboard", "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80");
        PRODUCT_IMAGES.put("pc-fan", "https://images.unsplash.com/photo-1624705002806-5d72df19c3ad?auto=format&fit=crop&w=900&q=80");

        initializeProducts();
    }

    private static void initializeProducts() {
        // Laptops
        products.add(makeProduct(1, "laptops", "Dell XPS 13 Plus", "Dell", 32990000, "Top seller",
                Map.ofEntries(Map.entry("purpose", "Business"), Map.entry("cpu", "Intel Core Ultra 7"), Map.entry("gpu", "Integrated"), Map.entry("ram", "16GB"), Map.entry("storage", "1TB SSD"), Map.entry("display", "13.4 inch 3.5K OLED"), Map.entry("battery", "55Wh"))));
        products.add(makeProduct(2, "laptops", "ASUS ROG Zephyrus G14", "ASUS", 42990000, "Gaming pick",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("cpu", "AMD Ryzen 9"), Map.entry("gpu", "RTX 4070"), Map.entry("ram", "32GB"), Map.entry("storage", "1TB SSD"), Map.entry("display", "14 inch QHD 165Hz"), Map.entry("battery", "76Wh"))));
        products.add(makeProduct(3, "laptops", "Lenovo ThinkPad P16", "Lenovo", 46990000, "Workstation",
                Map.ofEntries(Map.entry("purpose", "Workstation"), Map.entry("cpu", "Intel Core i7"), Map.entry("gpu", "RTX 4060"), Map.entry("ram", "32GB"), Map.entry("storage", "2TB SSD"), Map.entry("display", "16 inch OLED"), Map.entry("battery", "90Wh"))));
        products.add(makeProduct(4, "laptops", "HP Pavilion 15", "HP", 16990000, "Best value",
                Map.ofEntries(Map.entry("purpose", "Student"), Map.entry("cpu", "Intel Core i5"), Map.entry("gpu", "Integrated"), Map.entry("ram", "8GB"), Map.entry("storage", "512GB SSD"), Map.entry("display", "15.6 inch FHD"), Map.entry("battery", "41Wh"))));
        products.add(makeProduct(5, "laptops", "Acer Swift Go 14", "Acer", 21990000, "OLED value",
                Map.ofEntries(Map.entry("purpose", "Student"), Map.entry("cpu", "Intel Core Ultra 5"), Map.entry("gpu", "Integrated"), Map.entry("ram", "16GB"), Map.entry("storage", "512GB SSD"), Map.entry("display", "14 inch 2.8K OLED"), Map.entry("battery", "65Wh"))));

        // Mouse
        products.add(makeProduct(6, "mouse", "Logitech G Pro X Superlight 2", "Logitech", 3290000, "Esports",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("sensor", "Hero 25K"), Map.entry("dpi", "32000 DPI"), Map.entry("connection", "2.4GHz"), Map.entry("weight", "60g"))));
        products.add(makeProduct(7, "mouse", "Razer DeathAdder V3 Pro", "Razer", 3490000, "Ergonomic",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("sensor", "Focus Pro"), Map.entry("dpi", "30000 DPI"), Map.entry("connection", "2.4GHz"), Map.entry("weight", "63g"))));
        products.add(makeProduct(8, "mouse", "SteelSeries Aerox 5 Wireless", "SteelSeries", 2490000, "Lightweight",
                Map.ofEntries(Map.entry("purpose", "Wireless"), Map.entry("sensor", "TrueMove Air"), Map.entry("dpi", "18000 DPI"), Map.entry("connection", "Bluetooth"), Map.entry("weight", "74g"))));
        products.add(makeProduct(9, "mouse", "Corsair Katar Elite Wireless", "Corsair", 1590000, "Compact",
                Map.ofEntries(Map.entry("purpose", "Office"), Map.entry("sensor", "PixArt 3395"), Map.entry("dpi", "26000 DPI"), Map.entry("connection", "2.4GHz"), Map.entry("weight", "69g"))));
        products.add(makeProduct(10, "mouse", "ASUS ROG Keris AimPoint", "ASUS", 2190000, "FPS ready",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("sensor", "ROG AimPoint"), Map.entry("dpi", "36000 DPI"), Map.entry("connection", "USB-C wired"), Map.entry("weight", "75g"))));

        // Keyboards
        products.add(makeProduct(11, "keyboards", "Keychron K2 Pro", "Keychron", 2490000, "Wireless",
                Map.ofEntries(Map.entry("purpose", "Office"), Map.entry("switchType", "Brown"), Map.entry("layout", "75%"), Map.entry("connection", "Bluetooth"), Map.entry("backlight", "RGB"))));
        products.add(makeProduct(12, "keyboards", "Logitech G Pro X TKL", "Logitech", 3290000, "Tournament",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("switchType", "Red"), Map.entry("layout", "TKL"), Map.entry("connection", "2.4GHz"), Map.entry("backlight", "RGB"))));
        products.add(makeProduct(13, "keyboards", "Razer Huntsman Mini", "Razer", 2590000, "Compact",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("switchType", "Optical"), Map.entry("layout", "60%"), Map.entry("connection", "USB-C wired"), Map.entry("backlight", "RGB"))));
        products.add(makeProduct(14, "keyboards", "Akko 5075B Plus", "Akko", 1990000, "Hot-swap",
                Map.ofEntries(Map.entry("purpose", "Compact"), Map.entry("switchType", "Blue"), Map.entry("layout", "75%"), Map.entry("connection", "Bluetooth"), Map.entry("backlight", "RGB"))));
        products.add(makeProduct(15, "keyboards", "Corsair K70 RGB Pro", "Corsair", 3490000, "Full-size",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("switchType", "Red"), Map.entry("layout", "Full-size"), Map.entry("connection", "USB-C wired"), Map.entry("backlight", "RGB"))));

        // Monitors
        products.add(makeProduct(16, "monitors", "LG UltraGear 27GP850", "LG", 7290000, "QHD gaming",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("size", "27 inch"), Map.entry("resolution", "QHD"), Map.entry("refreshRate", "165Hz"), Map.entry("panel", "Nano IPS"))));
        products.add(makeProduct(17, "monitors", "Samsung Odyssey G5 32", "Samsung", 6790000, "Curved",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("size", "32 inch"), Map.entry("resolution", "QHD"), Map.entry("refreshRate", "144Hz"), Map.entry("panel", "VA"))));
        products.add(makeProduct(18, "monitors", "Dell UltraSharp U2723QE", "Dell", 12990000, "Creator",
                Map.ofEntries(Map.entry("purpose", "Design"), Map.entry("size", "27 inch"), Map.entry("resolution", "4K UHD"), Map.entry("refreshRate", "60Hz"), Map.entry("panel", "IPS Black"))));
        products.add(makeProduct(19, "monitors", "ASUS ProArt PA278QV", "ASUS", 6990000, "Color work",
                Map.ofEntries(Map.entry("purpose", "Design"), Map.entry("size", "27 inch"), Map.entry("resolution", "QHD"), Map.entry("refreshRate", "75Hz"), Map.entry("panel", "IPS"))));
        products.add(makeProduct(20, "monitors", "AOC 24G2SP", "AOC", 3990000, "Budget gaming",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("size", "24 inch"), Map.entry("resolution", "Full HD"), Map.entry("refreshRate", "165Hz"), Map.entry("panel", "IPS"))));

        // SSD
        products.add(makeProduct(21, "ssd", "Samsung 990 Pro 1TB", "Samsung", 2890000, "PCIe 4.0",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("capacity", "1TB"), Map.entry("interfaceType", "PCIe 4.0"), Map.entry("readSpeed", "7450MB/s"), Map.entry("formFactor", "M.2 2280"))));
        products.add(makeProduct(22, "ssd", "WD Black SN850X 2TB", "WD", 4590000, "High speed",
                Map.ofEntries(Map.entry("purpose", "Creator"), Map.entry("capacity", "2TB"), Map.entry("interfaceType", "PCIe 4.0"), Map.entry("readSpeed", "7300MB/s"), Map.entry("formFactor", "M.2 2280"))));
        products.add(makeProduct(23, "ssd", "Kingston NV2 1TB", "Kingston", 1490000, "Budget NVMe",
                Map.ofEntries(Map.entry("purpose", "Boot drive"), Map.entry("capacity", "1TB"), Map.entry("interfaceType", "PCIe 3.0"), Map.entry("readSpeed", "3500MB/s"), Map.entry("formFactor", "M.2 2280"))));
        products.add(makeProduct(24, "ssd", "Crucial MX500 1TB", "Crucial", 1690000, "SATA reliable",
                Map.ofEntries(Map.entry("purpose", "Boot drive"), Map.entry("capacity", "1TB"), Map.entry("interfaceType", "SATA"), Map.entry("readSpeed", "560MB/s"), Map.entry("formFactor", "2.5 inch"))));
        products.add(makeProduct(25, "ssd", "Seagate FireCuda 540 2TB", "Seagate", 6290000, "PCIe 5.0",
                Map.ofEntries(Map.entry("purpose", "Creator"), Map.entry("capacity", "2TB"), Map.entry("interfaceType", "PCIe 5.0"), Map.entry("readSpeed", "10000MB/s"), Map.entry("formFactor", "M.2 2280"))));

        // RAM
        products.add(makeProduct(26, "ram", "Corsair Vengeance DDR5 32GB", "Corsair", 2990000, "DDR5 kit",
                Map.ofEntries(Map.entry("purpose", "Gaming PC"), Map.entry("capacity", "32GB"), Map.entry("memoryType", "DDR5"), Map.entry("bus", "6000MHz"), Map.entry("formFactor", "DIMM"))));
        products.add(makeProduct(27, "ram", "Kingston Fury Beast 16GB", "Kingston", 1290000, "Gaming value",
                Map.ofEntries(Map.entry("purpose", "Gaming PC"), Map.entry("capacity", "16GB"), Map.entry("memoryType", "DDR4"), Map.entry("bus", "3200MHz"), Map.entry("formFactor", "DIMM"))));
        products.add(makeProduct(28, "ram", "G.Skill Trident Z5 RGB 32GB", "G.Skill", 3490000, "RGB build",
                Map.ofEntries(Map.entry("purpose", "RGB build"), Map.entry("capacity", "32GB"), Map.entry("memoryType", "DDR5"), Map.entry("bus", "6000MHz"), Map.entry("formFactor", "DIMM"))));
        products.add(makeProduct(29, "ram", "Crucial Laptop DDR4 16GB", "Crucial", 990000, "Laptop upgrade",
                Map.ofEntries(Map.entry("purpose", "Laptop upgrade"), Map.entry("capacity", "16GB"), Map.entry("memoryType", "DDR4"), Map.entry("bus", "3200MHz"), Map.entry("formFactor", "SODIMM"))));
        products.add(makeProduct(30, "ram", "TeamGroup Elite DDR5 16GB", "TeamGroup", 1490000, "DDR5 value",
                Map.ofEntries(Map.entry("purpose", "Laptop upgrade"), Map.entry("capacity", "16GB"), Map.entry("memoryType", "DDR5"), Map.entry("bus", "5200MHz"), Map.entry("formFactor", "SODIMM"))));

        // CPU
        products.add(makeProduct(31, "cpu", "Intel Core i5-14600K", "Intel", 7890000, "Gaming value",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("socket", "LGA1700"), Map.entry("cores", "14 cores"), Map.entry("threads", "20 threads"), Map.entry("tdp", "125W"))));
        products.add(makeProduct(32, "cpu", "Intel Core i7-14700K", "Intel", 11290000, "Creator pick",
                Map.ofEntries(Map.entry("purpose", "Streaming"), Map.entry("socket", "LGA1700"), Map.entry("cores", "20 cores"), Map.entry("threads", "28 threads"), Map.entry("tdp", "125W"))));
        products.add(makeProduct(33, "cpu", "AMD Ryzen 5 7600", "AMD", 4990000, "AM5 value",
                Map.ofEntries(Map.entry("purpose", "Office"), Map.entry("socket", "AM5"), Map.entry("cores", "6 cores"), Map.entry("threads", "12 threads"), Map.entry("tdp", "65W"))));
        products.add(makeProduct(34, "cpu", "AMD Ryzen 7 7800X3D", "AMD", 9690000, "Gaming king",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("socket", "AM5"), Map.entry("cores", "8 cores"), Map.entry("threads", "16 threads"), Map.entry("tdp", "120W"))));
        products.add(makeProduct(35, "cpu", "AMD Ryzen 9 7950X", "AMD", 13990000, "Workstation",
                Map.ofEntries(Map.entry("purpose", "Workstation"), Map.entry("socket", "AM5"), Map.entry("cores", "16 cores"), Map.entry("threads", "32 threads"), Map.entry("tdp", "170W"))));

        // GPU
        products.add(makeProduct(36, "gpu", "ASUS Dual RTX 4060 OC", "ASUS", 7990000, "1080p",
                Map.ofEntries(Map.entry("purpose", "1080p gaming"), Map.entry("chipset", "RTX 4060"), Map.entry("vram", "8GB"), Map.entry("power", "115W"), Map.entry("ports", "HDMI, DisplayPort"))));
        products.add(makeProduct(37, "gpu", "MSI RTX 4070 Super Gaming X", "MSI", 17990000, "1440p",
                Map.ofEntries(Map.entry("purpose", "1440p gaming"), Map.entry("chipset", "RTX 4070 Super"), Map.entry("vram", "12GB"), Map.entry("power", "220W"), Map.entry("ports", "HDMI, 3x DisplayPort"))));
        products.add(makeProduct(38, "gpu", "Gigabyte RTX 4080 Super Aero", "Gigabyte", 31990000, "4K",
                Map.ofEntries(Map.entry("purpose", "4K gaming"), Map.entry("chipset", "RTX 4080"), Map.entry("vram", "16GB"), Map.entry("power", "320W"), Map.entry("ports", "HDMI, 3x DisplayPort"))));
        products.add(makeProduct(39, "gpu", "Sapphire Pulse RX 7800 XT", "Sapphire", 13990000, "Radeon value",
                Map.ofEntries(Map.entry("purpose", "1440p gaming"), Map.entry("chipset", "RX 7800 XT"), Map.entry("vram", "16GB"), Map.entry("power", "263W"), Map.entry("ports", "HDMI, DisplayPort"))));
        products.add(makeProduct(40, "gpu", "Zotac RTX 4090 Trinity", "Zotac", 46990000, "AI work",
                Map.ofEntries(Map.entry("purpose", "AI work"), Map.entry("chipset", "RTX 4090"), Map.entry("vram", "24GB"), Map.entry("power", "450W"), Map.entry("ports", "HDMI, 3x DisplayPort"))));

        // PC Case
        products.add(makeProduct(41, "pc-case", "NZXT H5 Flow", "NZXT", 2190000, "Airflow",
                Map.ofEntries(Map.entry("purpose", "Airflow"), Map.entry("caseType", "Mid tower"), Map.entry("motherboardSupport", "ATX"), Map.entry("color", "Black"), Map.entry("fanSupport", "6 fans"))));
        products.add(makeProduct(42, "pc-case", "Corsair 4000D Airflow", "Corsair", 2490000, "Clean build",
                Map.ofEntries(Map.entry("purpose", "Airflow"), Map.entry("caseType", "Mid tower"), Map.entry("motherboardSupport", "ATX"), Map.entry("color", "White"), Map.entry("fanSupport", "6 fans"))));
        products.add(makeProduct(43, "pc-case", "Lian Li O11 Dynamic EVO", "Lian Li", 3990000, "Showcase",
                Map.ofEntries(Map.entry("purpose", "Showcase"), Map.entry("caseType", "Mid tower"), Map.entry("motherboardSupport", "E-ATX"), Map.entry("color", "White"), Map.entry("fanSupport", "10 fans"))));
        products.add(makeProduct(44, "pc-case", "Cooler Master NR200P", "Cooler Master", 2390000, "Mini ITX",
                Map.ofEntries(Map.entry("purpose", "Compact"), Map.entry("caseType", "Mini tower"), Map.entry("motherboardSupport", "Mini-ITX"), Map.entry("color", "Black"), Map.entry("fanSupport", "5 fans"))));
        products.add(makeProduct(45, "pc-case", "DeepCool CH560 Digital", "DeepCool", 2890000, "Display panel",
                Map.ofEntries(Map.entry("purpose", "Showcase"), Map.entry("caseType", "Mid tower"), Map.entry("motherboardSupport", "ATX"), Map.entry("color", "Black"), Map.entry("fanSupport", "9 fans"))));

        // Mainboard
        products.add(makeProduct(46, "mainboard", "ASUS TUF Gaming B650-Plus", "ASUS", 5290000, "AM5 durable",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("socket", "AM5"), Map.entry("chipset", "B650"), Map.entry("formFactor", "ATX"), Map.entry("memoryType", "DDR5"))));
        products.add(makeProduct(47, "mainboard", "MSI MAG B760 Tomahawk WiFi", "MSI", 4890000, "Intel value",
                Map.ofEntries(Map.entry("purpose", "Gaming"), Map.entry("socket", "LGA1700"), Map.entry("chipset", "B760"), Map.entry("formFactor", "ATX"), Map.entry("memoryType", "DDR5"))));
        products.add(makeProduct(48, "mainboard", "Gigabyte Z790 Aorus Elite AX", "Gigabyte", 7190000, "Overclock",
                Map.ofEntries(Map.entry("purpose", "Overclocking"), Map.entry("socket", "LGA1700"), Map.entry("chipset", "Z790"), Map.entry("formFactor", "ATX"), Map.entry("memoryType", "DDR5"))));
        products.add(makeProduct(49, "mainboard", "ASRock B550M Steel Legend", "ASRock", 2890000, "AM4 value",
                Map.ofEntries(Map.entry("purpose", "Budget PC"), Map.entry("socket", "AM4"), Map.entry("chipset", "B550"), Map.entry("formFactor", "Micro-ATX"), Map.entry("memoryType", "DDR4"))));
        products.add(makeProduct(50, "mainboard", "Biostar B650MT", "Biostar", 3290000, "Budget AM5",
                Map.ofEntries(Map.entry("purpose", "Budget PC"), Map.entry("socket", "AM5"), Map.entry("chipset", "B650"), Map.entry("formFactor", "Micro-ATX"), Map.entry("memoryType", "DDR5"))));

        // PC Fan
        products.add(makeProduct(51, "pc-fan", "Noctua NF-A12x25 PWM", "Noctua", 890000, "Silent",
                Map.ofEntries(Map.entry("purpose", "Silent"), Map.entry("size", "120mm"), Map.entry("speed", "2000 RPM"), Map.entry("airflow", "60 CFM"), Map.entry("lighting", "None"))));
        products.add(makeProduct(52, "pc-fan", "Corsair iCUE AF120 RGB Elite", "Corsair", 690000, "RGB",
                Map.ofEntries(Map.entry("purpose", "RGB"), Map.entry("size", "120mm"), Map.entry("speed", "2100 RPM"), Map.entry("airflow", "65 CFM"), Map.entry("lighting", "RGB"))));
        products.add(makeProduct(53, "pc-fan", "Cooler Master SickleFlow 120", "Cooler Master", 290000, "Budget RGB",
                Map.ofEntries(Map.entry("purpose", "RGB"), Map.entry("size", "120mm"), Map.entry("speed", "1800 RPM"), Map.entry("airflow", "62 CFM"), Map.entry("lighting", "RGB"))));
        products.add(makeProduct(54, "pc-fan", "DeepCool FK120", "DeepCool", 350000, "Radiator",
                Map.ofEntries(Map.entry("purpose", "Radiator"), Map.entry("size", "120mm"), Map.entry("speed", "1850 RPM"), Map.entry("airflow", "68 CFM"), Map.entry("lighting", "None"))));
        products.add(makeProduct(55, "pc-fan", "Arctic P14 PWM PST", "Arctic", 390000, "140mm airflow",
                Map.ofEntries(Map.entry("purpose", "High airflow"), Map.entry("size", "140mm"), Map.entry("speed", "1700 RPM"), Map.entry("airflow", "72 CFM"), Map.entry("lighting", "None"))));
    }

    private static Product makeProduct(int id, String categoryId, String name, String brand, long price,
            String badge, Map<String, String> specs) {
        String categoryName = getCategoryName(categoryId);
        int stock = id % 9 == 0 ? 0 : 5 + (id % 12);
        String image = PRODUCT_IMAGES.getOrDefault(categoryId, "");
        String warranty = categoryId.equals("laptops") ? "24 months official warranty" : "12 months official warranty";
        String purpose = specs.getOrDefault("purpose", "").toLowerCase();
        String description = String.format("%s is a %s %s option for %s users.",
                name, badge.toLowerCase(), categoryName.toLowerCase(), purpose);

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Minh Anh", 4 + (id % 2), "2026-05-12",
                String.format("Good %s for the price. The specifications match my current setup.", categoryName.toLowerCase())));
        reviews.add(new Review("Hoang Nam", 4, "2026-05-18",
                "Delivery was quick and the product information was easy to compare before buying."));

        return new Product(id, categoryId, categoryName, name, brand, price, badge, specs,
                stock, image, warranty, description, reviews);
    }

    private static String getCategoryName(String categoryId) {
        switch (categoryId) {
            case "laptops":
                return "Laptops";
            case "mouse":
                return "Mouse";
            case "keyboards":
                return "Keyboards";
            case "monitors":
                return "Monitors";
            case "ssd":
                return "SSD";
            case "ram":
                return "RAM";
            case "cpu":
                return "CPU";
            case "gpu":
                return "GPU";
            case "pc-case":
                return "PC Case";
            case "mainboard":
                return "Mainboard";
            case "pc-fan":
                return "PC Fan";
            default:
                return categoryId;
        }
    }

    public static List<Product> getAll() {
        return new ArrayList<>(products);
    }

    public static Product getById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static List<Product> getByCategory(String categoryId) {
        return products.stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public static List<Product> search(String query, String categoryId, long minPrice, long maxPrice,
            Map<String, String> filters, String sortOrder) {
        String normalizedQuery = query == null ? "" : query.toLowerCase();
        List<Product> results = products.stream()
                .filter(p -> normalizedQuery.isEmpty() || getSearchableText(p).contains(normalizedQuery))
                .filter(p -> categoryId == null || categoryId.isEmpty() || categoryId.equals("all") || p.getCategoryId().equals(categoryId))
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() < maxPrice)
                .filter(p -> matchesFilters(p, filters))
                .collect(Collectors.toList());

        sortResults(results, sortOrder);
        return results;
    }

    private static String getSearchableText(Product product) {
        List<String> values = new ArrayList<>();
        values.add(product.getName());
        values.add(product.getBrand());
        values.add(product.getCategory());
        values.add(product.getBadge());
        values.addAll(product.getSpecs().values());
        return String.join(" ", values).toLowerCase();
    }

    private static boolean matchesFilters(Product product, Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if ("all".equals(filter.getValue())) {
                continue;
            }

            String specValue = "brand".equals(filter.getKey())
                    ? product.getBrand()
                    : product.getSpecs().get(filter.getKey());
            if (specValue == null || !specValue.equals(filter.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static void sortResults(List<Product> results, String sortOrder) {
        if (sortOrder == null || sortOrder.isEmpty() || sortOrder.equals("recommended")) {
            return;
        }

        if (sortOrder.equals("price-asc")) {
            results.sort(Comparator.comparingLong(Product::getPrice));
        } else if (sortOrder.equals("price-desc")) {
            results.sort((p1, p2) -> Long.compare(p2.getPrice(), p1.getPrice()));
        }
    }
}
