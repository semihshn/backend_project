package source_files.data.DTO.itemDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarModelDTO {
    int id;
    int brandEntityId;
    String name;
    String brandEntityName;
    boolean isDeleted;
}
