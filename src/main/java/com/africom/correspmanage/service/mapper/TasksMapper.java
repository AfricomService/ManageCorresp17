package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tasks} and its DTO {@link TasksDTO}.
 */
@Mapper(componentModel = "spring")
public interface TasksMapper extends EntityMapper<TasksDTO, Tasks> {
    @Mapping(target = "parentDoc", source = "parentDoc", qualifiedByName = "correspId")
    @Mapping(target = "parentTask", source = "parentTask", qualifiedByName = "tasksId")
    TasksDTO toDto(Tasks s);

    @Named("correspId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorrespDTO toDtoCorrespId(Corresp corresp);

    @Named("tasksId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TasksDTO toDtoTasksId(Tasks tasks);
}
