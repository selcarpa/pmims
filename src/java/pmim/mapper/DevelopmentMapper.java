package pmim.mapper;

import pmim.model.Development;
import pmim.model.SysUser;

import java.util.List;

public interface DevelopmentMapper {
    void insertDevelopment(Development d);

    List<Development> selectDevelopmentByIdUndeleted(SysUser sysUser);
}
