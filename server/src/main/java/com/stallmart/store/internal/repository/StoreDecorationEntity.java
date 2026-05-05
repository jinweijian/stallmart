package com.stallmart.store.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_decoration")
public class StoreDecorationEntity {
    @Id
    @Column(name = "store_id")
    public Long storeId;
    @Lob
    @Column(name = "banners_json", columnDefinition = "TEXT")
    public String bannersJson;
    @Lob
    @Column(name = "colors_json", columnDefinition = "TEXT")
    public String colorsJson;
    @Lob
    @Column(name = "icon_urls_json", columnDefinition = "TEXT")
    public String iconUrlsJson;
    @Lob
    @Column(name = "category_icon_urls_json", columnDefinition = "TEXT")
    public String categoryIconUrlsJson;
    @Lob
    @Column(name = "image_urls_json", columnDefinition = "TEXT")
    public String imageUrlsJson;
    @Lob
    @Column(name = "copywriting_json", columnDefinition = "TEXT")
    public String copywritingJson;
}
