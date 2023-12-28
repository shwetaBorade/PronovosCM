package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.pronovoscm.model.response.inventorycategories.Categories;
import com.pronovoscm.model.response.inventoryequipment.Equipments;
import com.pronovoscm.model.response.inventoryresponse.InvRes;
import com.pronovoscm.model.response.inventorysubcategories.InventorySubCategoriesResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMasterDao;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentInventoryDao;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentRegionDao;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMasterDao;
import com.pronovoscm.utils.DateFormatter;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class EquipementInventoryRepository extends AbstractRepository {
    private final Context context;


    public EquipementInventoryRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }


    /**
     * Insert or update EquipmentCategories
     *
     * @param categories
     * @return
     */
    public List<EquipmentCategoriesMaster> doUpdateEquipmentCategoriesMaster(List<Categories> categories, int userId) {

        try {

            getDaoSession().callInTx(new Callable<List<Categories>>() {
                final EquipmentCategoriesMasterDao mEquipmentCategoriesMasterDao = getDaoSession().getEquipmentCategoriesMasterDao();

                @Override
                public List<Categories> call() {
                    for (Categories category : categories) {
                        List<EquipmentCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                                EquipmentCategoriesMasterDao.Properties.Eq_categories_id.eq(category.getEq_categories_id()),
                                EquipmentCategoriesMasterDao.Properties.UsersId.eq(userId)
                        ).limit(1).list();
                        EquipmentCategoriesMaster eqCategory;
                        if (equipmentCategories.size() > 0) {
                            eqCategory = equipmentCategories.get(0);
                            eqCategory.setAllocation_uom(category.getAllocation_uom());
                            eqCategory.setUsersId(userId);
                            eqCategory.setName(category.getName());
                            eqCategory.setEq_categories_id(category.getEq_categories_id());
                            eqCategory.setUpdatedAt(category.getUpdatedAt() != null && !category.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getUpdatedAt()) : null);
                            eqCategory.setTenantId(category.getTenant_id());

                            mEquipmentCategoriesMasterDao.update(eqCategory);
                        } else {
                            eqCategory = new EquipmentCategoriesMaster();
                            eqCategory.setAllocation_uom(category.getAllocation_uom());
                            eqCategory.setUsersId(userId);
                            eqCategory.setName(category.getName());
                            eqCategory.setEq_categories_id(category.getEq_categories_id());
                            eqCategory.setUpdatedAt(category.getUpdatedAt() != null && !category.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getUpdatedAt()) : null);
                            eqCategory.setTenantId(category.getTenant_id());
                            mEquipmentCategoriesMasterDao.save(eqCategory);
                        }
                    }

                    return categories;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEquipmentCategories(userId);
    }

    /**
     * Insert or update EquipmentCategories
     *
     * @param categories
     * @return
     */
    public List<EquipmentSubCategoriesMaster> doUpdateEquipmentSubCategoriesMaster(List<InventorySubCategoriesResponse.Subcategories> categories, int userId) {
        try {
            getDaoSession().callInTx(new Callable<List<InventorySubCategoriesResponse.Subcategories>>() {
                final EquipmentSubCategoriesMasterDao mEquipmentCategoriesDao = getDaoSession().getEquipmentSubCategoriesMasterDao();

                @Override
                public List<InventorySubCategoriesResponse.Subcategories> call() {
                    for (InventorySubCategoriesResponse.Subcategories category : categories) {
                        List<EquipmentSubCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentSubCategoriesMasterDao().queryBuilder().where(
                                EquipmentSubCategoriesMasterDao.Properties.EqSubCategoryId.eq(category.getEq_sub_categories_id()),
                                EquipmentSubCategoriesMasterDao.Properties.UsersId.eq(userId)
                        ).limit(1).list();


                        EquipmentSubCategoriesMaster subCategoriesMaster;
                        if (equipmentCategories.size() > 0) {
                            subCategoriesMaster = equipmentCategories.get(0);
                            subCategoriesMaster.setUsersId(userId);
                            subCategoriesMaster.setTenant_id(category.getTenantId());
                            subCategoriesMaster.setName(category.getName());
                            subCategoriesMaster.setEqCategoryId(category.getEq_categories_id());
                            subCategoriesMaster.setEqSubCategoryId(category.getEq_sub_categories_id());
                            subCategoriesMaster.setUpdatedAt(category.getUpdatedAt() != null && !category.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getUpdatedAt()) : null);
                            subCategoriesMaster.setDeletedAt(category.getDeletedAt() != null && !category.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getDeletedAt()) : null);
                            mEquipmentCategoriesDao.update(subCategoriesMaster);
                        } else {

                            subCategoriesMaster = new EquipmentSubCategoriesMaster();
                            subCategoriesMaster.setUsersId(userId);
                            subCategoriesMaster.setTenant_id(category.getTenantId());
                            subCategoriesMaster.setName(category.getName());
                            subCategoriesMaster.setEqCategoryId(category.getEq_categories_id());
                            subCategoriesMaster.setEqSubCategoryId(category.getEq_sub_categories_id());
                            subCategoriesMaster.setUpdatedAt(category.getUpdatedAt() != null && !category.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getUpdatedAt()) : null);
                            subCategoriesMaster.setDeletedAt(category.getDeletedAt() != null && !category.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(category.getDeletedAt()) : null);
                            mEquipmentCategoriesDao.save(subCategoriesMaster);
                        }
                    }

                    return categories;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
//        return getEquipmentSubCategoriesMaster(projectId,eqCategoryId, userId);
    }

    /**
     * Insert or update EquipmentCategories
     *
     * @param inventoryList
     * @return
     */
    public List<EquipmentInventory> doUpdateEquipmentInventory(List<InvRes.Inventory> inventoryList, int userId, int projectId) {
        try {

            getDaoSession().callInTx(new Callable<List<InvRes.Inventory>>() {
                final EquipmentInventoryDao mEquipmentCategoriesDao = getDaoSession().getEquipmentInventoryDao();

                @Override
                public List<InvRes.Inventory> call() {
                    for (InvRes.Inventory inventory : inventoryList) {
                        List<EquipmentInventory> equipmentCategories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                                EquipmentInventoryDao.Properties.EqInventoryId.eq(inventory.getEqInventoryId()),
                                EquipmentInventoryDao.Properties.CompanyIdNumber.eq(inventory.getCompanyIdNumber()),
                                EquipmentInventoryDao.Properties.PjProjectsId.eq(projectId),
//                                EquipmentInventoryDao.Properties.Status.eq(inventory.getStatus()),
//                                EquipmentInventoryDao.Properties.EquipmentStatus.eq(inventory.getEquipmentStatus()),
                                EquipmentInventoryDao.Properties.UsersId.eq(0)
                        ).limit(1).list();

                        EquipmentInventory equipmentInventory;
                        if (equipmentCategories.size() > 0) {
                            equipmentInventory = equipmentCategories.get(0);
                            equipmentInventory.setEqInventoryId(inventory.getEqInventoryId());
                            equipmentInventory.setEqRegionEquipentId(inventory.getEqRegionEquipmentId());
                            equipmentInventory.setPjProjectsId(inventory.getPjProjectsId());
//                            equipmentInventory.setReason(inventory.getAdjustmentReason());
//                            equipmentInventory.setOwner(inventory.getOwner());
//                            equipmentInventory.setManager(inventory.getmanager());
                            equipmentInventory.setQuantity(inventory.getQuantity());
                            equipmentInventory.setPurchasedFrom(inventory.getPurchasedFrom());
                            equipmentInventory.setPurchase_price(inventory.getPurchasePrice());
                            equipmentInventory.setCompanyIdNumber(inventory.getCompanyIdNumber());
                            equipmentInventory.setAdjustmentReason(inventory.getAdjustmentReason());
                            equipmentInventory.setNotes(inventory.getNotes());
                            equipmentInventory.setUpdatedAt(inventory.getUpdatedAt() != null && !inventory.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(inventory.getUpdatedAt()) : null);
                            equipmentInventory.setDeletedAt(inventory.getDeletedAt() != null && !inventory.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(inventory.getDeletedAt()) : null);
                            equipmentInventory.setStatus(inventory.getStatus());
                            equipmentInventory.setEquipmentStatus(inventory.getEquipmentStatus());
//                            equipmentInventory.setAvailableQuantity(inventory.getavailableQuantity());
                            equipmentInventory.setUsersId(0);
                            equipmentInventory.setTenant_id(inventory.getTenantId());
                            equipmentInventory.setEqSubCategoryId(inventory.getEqSubCategoriesId());
                            mEquipmentCategoriesDao.update(equipmentInventory);
                        } else {

                            equipmentInventory = new EquipmentInventory();
                            equipmentInventory.setEqInventoryId(inventory.getEqInventoryId());
                            equipmentInventory.setEqRegionEquipentId(inventory.getEqRegionEquipmentId());
                            equipmentInventory.setPjProjectsId(inventory.getPjProjectsId());
//                            equipmentInventory.setReason(inventory.getAdjustmentReason());
//                            equipmentInventory.setOwner(inventory.getOwner());
//                            equipmentInventory.setManager(inventory.getmanager());
                            equipmentInventory.setQuantity(inventory.getQuantity());
                            equipmentInventory.setPurchasedFrom(inventory.getPurchasedFrom());
                            equipmentInventory.setPurchase_price(inventory.getPurchasePrice());
                            equipmentInventory.setCompanyIdNumber(inventory.getCompanyIdNumber());
                            equipmentInventory.setAdjustmentReason(inventory.getAdjustmentReason());
                            equipmentInventory.setNotes(inventory.getNotes());
                            equipmentInventory.setUpdatedAt(inventory.getUpdatedAt() != null && !inventory.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(inventory.getUpdatedAt()) : null);
                            equipmentInventory.setDeletedAt(inventory.getDeletedAt() != null && !inventory.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(inventory.getDeletedAt()) : null);
                            equipmentInventory.setStatus(inventory.getStatus());
                            equipmentInventory.setEquipmentStatus(inventory.getEquipmentStatus());
//                            equipmentInventory.setAvailableQuantity(inventory.getavailableQuantity());
                            equipmentInventory.setUsersId(0);
                            equipmentInventory.setTenant_id(inventory.getTenantId());
                            equipmentInventory.setEqSubCategoryId(inventory.getEqSubCategoriesId());
                            mEquipmentCategoriesDao.save(equipmentInventory);
                        }
                    }

                    return inventoryList;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
//        return getEquipmentInventory(projectId,eqCategoryId, userId);
    }

    /**
     * Insert or update EquipmentCategories
     *
     * @param equipmentsList
     * @return
     */
    public List<EquipmentRegion> doUpdateEquipmentDetails(List<Equipments> equipmentsList, int userId) {
        try {

            getDaoSession().callInTx(new Callable<List<Equipments>>() {
                final EquipmentRegionDao mEquipmentCategoriesDao = getDaoSession().getEquipmentRegionDao();

                @Override
                public List<Equipments> call() {
                    for (Equipments equipments : equipmentsList) {
                        List<EquipmentRegion> equipmentCategories = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                                EquipmentRegionDao.Properties.EqSubCategoryId.eq(equipments.getEqSubCategoriesId()),
                                EquipmentRegionDao.Properties.EqRegionEquipentId.eq(equipments.getEqRegionEquipmentId()),
                                EquipmentRegionDao.Properties.Tenant_id.eq(equipments.getTenantId()),
                                EquipmentRegionDao.Properties.UsersId.eq(userId)
                        ).limit(1).list();


                        EquipmentRegion equipmentRegion;
                        if (equipmentCategories.size() > 0) {
                            equipmentRegion = equipmentCategories.get(0);
                            equipmentRegion.setUsersId(userId);
                            equipmentRegion.setName(equipments.getName());
                            equipmentRegion.setUpc(equipments.getUpc());
                            equipmentRegion.setEqSubCategoryId(equipments.getEqSubCategoriesId());
                            equipmentRegion.setAllocated(equipments.getAllocated());
                            equipmentRegion.setAllocated_qty(equipments.getAllocatedQty());
                            equipmentRegion.setEqRegionEquipentId(equipments.getEqRegionEquipmentId());
                            equipmentRegion.setRegionsId(equipments.getRegionsId());
                            equipmentRegion.setForecasted(equipments.getForecasted());
                            equipmentRegion.setItemsPerUnit(equipments.getItemsPerUnit());
                            equipmentRegion.setUpdatedAt(equipments.getUpdatedAt() != null && !equipments.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(equipments.getUpdatedAt()) : null);

                            equipmentRegion.setManufacturer(equipments.getManufacturer());
                            equipmentRegion.setPicture(equipments.getPicture());
                            equipmentRegion.setType(equipments.getType());
                            equipmentRegion.setNotes(equipments.getNotes());
                            equipmentRegion.setWeight(equipments.getWeight());
                            equipmentRegion.setModel(equipments.getModel());
                            equipmentRegion.setTenant_id(equipments.getTenantId());
                            mEquipmentCategoriesDao.update(equipmentRegion);
                        } else {
                            equipmentRegion = new EquipmentRegion();
                            equipmentRegion.setUsersId(userId);
                            equipmentRegion.setName(equipments.getName());
                            equipmentRegion.setUpc(equipments.getUpc());
                            equipmentRegion.setEqSubCategoryId(equipments.getEqSubCategoriesId());
                            equipmentRegion.setAllocated(equipments.getAllocated());
                            equipmentRegion.setAllocated_qty(equipments.getAllocatedQty());
                            equipmentRegion.setEqRegionEquipentId(equipments.getEqRegionEquipmentId());
                            equipmentRegion.setRegionsId(equipments.getRegionsId());
                            equipmentRegion.setForecasted(equipments.getForecasted());
                            equipmentRegion.setItemsPerUnit(equipments.getItemsPerUnit());
                            equipmentRegion.setUpdatedAt(equipments.getUpdatedAt() != null && !equipments.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(equipments.getUpdatedAt()) : null);
                            equipmentRegion.setTenant_id(equipments.getTenantId());

                            equipmentRegion.setManufacturer(equipments.getManufacturer());
                            equipmentRegion.setPicture(equipments.getPicture());
                            equipmentRegion.setType(equipments.getType());
                            equipmentRegion.setNotes(equipments.getNotes());
                            equipmentRegion.setWeight(equipments.getWeight());
                            equipmentRegion.setModel(equipments.getModel());
                            mEquipmentCategoriesDao.save(equipmentRegion);
                        }
                    }

                    return equipmentsList;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
//        return getEquipmentDetails(projectId,eqCategoryId, userId,eqSubCategoryId);
    }

    /**
     * List of EquipmentCategories
     *
     * @param userId
     * @return
     */
    public List<EquipmentCategoriesMaster> getEquipmentCategories(int userId) {
        return getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(EquipmentCategoriesMasterDao.Properties.UsersId.eq(0)).list();
    }
    /**
     * List of EquipmentSubCategories
     *
     * @param projectId
     * @param userId
     * @return
     */
   /* public List<EquipmentSubCategories> getEquipmentSubCategories(int projectId, int eqCategoryId, int userId) {
        return getDaoSession().getEquipmentSubCategoriesDao().queryBuilder().where(
                EquipmentSubCategoriesDao.Properties.PjProjectsId.eq(projectId),
                EquipmentSubCategoriesDao.Properties.EqCategoryId.eq(eqCategoryId),
                EquipmentSubCategoriesDao.Properties.UsersId.eq(userId)).list();
    }
*//*
     *//**
     * List of EquipmentCategories
     *
     * @param projectId
     * @param userId
     * @return
     *//*
    public List<EquipmentCategoriesDetails> getEquipmentDetails(int projectId, int eqCategoryId, int userId, int eqSubCategoryId) {
        return getDaoSession().getEquipmentCategoriesDetailsDao().queryBuilder().where(
                EquipmentCategoriesDetailsDao.Properties.PjProjectsId.eq(projectId),
                EquipmentCategoriesDetailsDao.Properties.EqCategoryId.eq(eqCategoryId),
                EquipmentCategoriesDetailsDao.Properties.EqSubCategoryId.eq(eqSubCategoryId),
                EquipmentCategoriesDetailsDao.Properties.UsersId.eq(userId)).list();
    }*/

    /**
     * List of EquipmentCategories
     *
     * @param id
     * @param userId
     * @return
     */
  /*  public EquipmentCategories getEquipmentCategory(long id, int userId) {
        List<EquipmentCategories> equipmentCategories=getDaoSession().getEquipmentCategoriesDao().queryBuilder().where(
                EquipmentCategoriesDao.Properties.Id.eq(id),
                EquipmentCategoriesDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (equipmentCategories.size()>0)
        return equipmentCategories.get(0);
        else return null;
    }
  */

    /**
     * List of EquipmentCategories
     *
     * @param userId
     * @return
     */
 /*   public EquipmentCategories getEquipmentCategoryByCategoryId(long id, int userId) {
        List<EquipmentCategories> equipmentCategories=getDaoSession().getEquipmentCategoriesDao().queryBuilder().where(
                EquipmentCategoriesDao.Properties.Eq_categories_id.eq(id),
                EquipmentCategoriesDao.Properties.UsersId.eq(userId)).limit(1).list();
        if (equipmentCategories.size()>0)
        return equipmentCategories.get(0);
        else return null;
    }*/
    public List<EquipmentCategoriesMaster> getCategories(int projectId, int userId) {
        userId = 0;
        Query query = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + userId + " AND eq_categories_id IN " +
                        "(SELECT eq_categories_id FROM eq_sub_categories where users_id = " + userId + " AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_inventory where users_id = " + userId + " AND pj_projects_id = " + projectId + " AND deleted_at Is NULL AND quantity != 0))"))
                .orderAsc(EquipmentCategoriesMasterDao.Properties.Name).build();
        return (List<EquipmentCategoriesMaster>) query.list();
    }

    public List<EquipmentCategoriesMaster> getCategoriesAccordingTenantId(int tenantId, int userId) {
        userId = 0;
        List<EquipmentCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                EquipmentCategoriesMasterDao.Properties.TenantId.eq(tenantId),
                EquipmentCategoriesMasterDao.Properties.UsersId.eq(userId)).list();


        if (equipmentCategories != null) {
            return equipmentCategories;
        } else return new ArrayList<>();
    }

    public List<EquipmentSubCategoriesMaster> getSubCategories(int categoryId, int projectId, int userId) {
        userId = 0;

        Query query = getDaoSession().getEquipmentSubCategoriesMasterDao().queryBuilder().where(
                new WhereCondition.StringCondition("eq_categories_id = " + categoryId + " AND users_id = " + userId +
                        " AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_inventory where users_id = " + userId +
                        " AND pj_projects_id = " + projectId + " AND quantity != 0)"))
                .orderAsc(EquipmentSubCategoriesMasterDao.Properties.Name).build();
        return (List<EquipmentSubCategoriesMaster>) query.list();
    }

    public List<EquipmentSubCategoriesMaster> getSubCategoriesAccordingTenantId(int categoryId, int userId) {
        userId = 0;
        List<EquipmentSubCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentSubCategoriesMasterDao().queryBuilder().where(
                EquipmentSubCategoriesMasterDao.Properties.EqCategoryId.eq(categoryId),
                EquipmentSubCategoriesMasterDao.Properties.UsersId.eq(userId)).list();
     /*   Query query = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + userId + " AND eq_categories_id IN " +
                        "(SELECT eq_categories_id FROM eq_sub_categories where users_id = " + userId + " AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_inventory where users_id = " + userId + " AND pj_projects_id = " + projectId + " AND deleted_at Is NULL AND quantity != 0))"))
                .orderAsc(EquipmentCategoriesMasterDao.Properties.Name).build();
     */

        if (equipmentCategories != null) {
            return equipmentCategories;
        } else return new ArrayList<>();
    }

    public EquipmentCategoriesMaster getEquipmentCategory(long categoryId, int users_id) {
        users_id = 0;
        List<EquipmentCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                EquipmentCategoriesMasterDao.Properties.Id.eq(categoryId),
                EquipmentCategoriesMasterDao.Properties.UsersId.eq(users_id)).limit(1).list();
        if (equipmentCategories.size() > 0)
            return equipmentCategories.get(0);
        else return null;

    }

    public EquipmentCategoriesMaster getEquipmentCategoryByCategoryId(int categoryId, int users_id) {
        users_id = 0;
        List<EquipmentCategoriesMaster> equipmentCategories = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                EquipmentCategoriesMasterDao.Properties.Eq_categories_id.eq(categoryId),
                EquipmentCategoriesMasterDao.Properties.UsersId.eq(users_id)).limit(1).list();
        if (equipmentCategories.size() > 0)
            return equipmentCategories.get(0);
        else return null;
    }

    public List<EquipmentInventory> getEquipmentInventory(int pj_projects_id, int eq_region_equipment_id, int users_id) {
        List<EquipmentInventory> equipmentCategories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(eq_region_equipment_id),
                EquipmentInventoryDao.Properties.PjProjectsId.eq(pj_projects_id),
                EquipmentInventoryDao.Properties.Status.eq(1)/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentCategories.size() > 0)
            return equipmentCategories;
        else return new ArrayList<>();
    }

    public boolean hasEquipmentInventoryList(int pj_projects_id) {
        List<EquipmentInventory> equipmentCategories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.PjProjectsId.eq(pj_projects_id)).list();
        if (equipmentCategories.size() > 0)
            return true;
        else return false;
    }

    public List<EquipmentInventory> getEquipmentInventory(int eq_region_equipment_id, int projectId) {
        List<EquipmentInventory> equipmentCategories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(eq_region_equipment_id),
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projectId),
                EquipmentInventoryDao.Properties.Status.eq(1),
                EquipmentInventoryDao.Properties.DeletedAt.isNull())
                .orderAsc(EquipmentInventoryDao.Properties.CompanyIdNumber).list();
        if (equipmentCategories.size() > 0)
            return equipmentCategories;
        else return new ArrayList<>();
    }

    public List<EquipmentInventory> checkgetEquipmentInventory(int eq_region_equipment_id, int users_id, int projectID) {
        List<EquipmentInventory> equipmentCategories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(eq_region_equipment_id),
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projectID)/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentCategories.size() >= 0)
            return equipmentCategories;
        else return new ArrayList<>();
    }

    public List<EquipmentRegion> getEquipmentRegion(Integer eqSubCategoryId, int users_id, int projetctId) {
        users_id = 0;
        Query query = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + users_id + " AND eq_region_equipment_id IN " +
                        "(SELECT eq_region_equipment_id FROM eq_inventory WHERE users_id = " + users_id + " AND pj_projects_id = " + projetctId +
                        " AND quantity != 0 AND deleted_at Is NULL AND eq_sub_categories_id =" + eqSubCategoryId + ")")).orderAsc(EquipmentRegionDao.Properties.Name).build();
        return (List<EquipmentRegion>) query.list();

    }

    public List<EquipmentRegion> getEquipmentRegionForTransfer(int tenantID, Integer eqSubCategoryId, int users_id) {
        users_id = 0;
        List<EquipmentRegion> equipmentCategories = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                EquipmentRegionDao.Properties.EqSubCategoryId.eq(eqSubCategoryId),
                EquipmentRegionDao.Properties.Tenant_id.eq(tenantID),
                EquipmentRegionDao.Properties.UsersId.eq(users_id)).list();
     /*   Query query = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + userId + " AND eq_categories_id IN " +
                        "(SELECT eq_categories_id FROM eq_sub_categories where users_id = " + userId + " AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_inventory where users_id = " + userId + " AND pj_projects_id = " + projectId + " AND deleted_at Is NULL AND quantity != 0))"))
                .orderAsc(EquipmentCategoriesMasterDao.Properties.Name).build();
     */

        if (equipmentCategories != null) {
            return equipmentCategories;
        } else return new ArrayList<>();

    }

    public List<EquipmentRegion> getEquipmentRegionAccordingTenant(int tenantID, int users_id) {
        users_id = 0;
        List<EquipmentRegion> equipmentCategories = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                EquipmentRegionDao.Properties.Tenant_id.eq(tenantID),
                EquipmentRegionDao.Properties.UsersId.eq(users_id))
                .orderAsc(EquipmentRegionDao.Properties.Name).list();
     /*   Query query = getDaoSession().getEquipmentCategoriesMasterDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + userId + " AND eq_categories_id IN " +
                        "(SELECT eq_categories_id FROM eq_sub_categories where users_id = " + userId + " AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_inventory where users_id = " + userId + " AND pj_projects_id = " + projectId + " AND deleted_at Is NULL AND quantity != 0))"))
                .orderAsc(EquipmentCategoriesMasterDao.Properties.Name).build();
     */

        if (equipmentCategories != null) {
            return equipmentCategories;
        } else return new ArrayList<>();

    }

    public EquipmentRegion getEquipmentRegion(Integer eqSubCategoryId) {

        Query query = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                EquipmentRegionDao.Properties.EqRegionEquipentId.eq(eqSubCategoryId))
                .orderAsc(EquipmentRegionDao.Properties.Name).build();
        return (EquipmentRegion) (query.list().size() <= 0 ? null : query.list().get(0));

    }

    public List<EquipmentRegion> getSearchEquipmentRegion(String string, int users_id, int projetctId) {
        users_id = 0;
        Query query = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                new WhereCondition.StringCondition("name LIKE '%" + string + "%' AND users_id = " + users_id + " AND eq_region_equipment_id IN " +
                        "(SELECT eq_region_equipment_id FROM eq_inventory WHERE users_id = " + users_id + " AND pj_projects_id = " + projetctId +
                        " AND quantity != 0 AND deleted_at Is NULL )")).orderAsc(EquipmentRegionDao.Properties.Name).build();
        return (List<EquipmentRegion>) query.list();
    }

    public int getCategoriesCount(Integer eqCategoryId, int users_id, int projetctId) {
        users_id = 0;
        Query query = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + users_id + " AND eq_region_equipment_id IN " +
                        "(SELECT eq_region_equipment_id FROM eq_inventory WHERE users_id = " + users_id + " AND pj_projects_id = " + projetctId +
                        " AND quantity != 0 AND deleted_at Is NULL AND eq_sub_categories_id IN " +
                        "(SELECT eq_sub_categories_id FROM eq_sub_categories WHERE users_id = " + users_id + " AND eq_categories_id = " + eqCategoryId + "))")).build();
        return query.list().size();
    }

    public int getSubCategoriesCount(Integer eqSubCategoryId, int users_id, int projetctId) {
        users_id = 0;
        Query query = getDaoSession().getEquipmentRegionDao().queryBuilder().where(
                new WhereCondition.StringCondition("users_id = " + users_id + " AND eq_region_equipment_id IN " +
                        "(SELECT eq_region_equipment_id FROM eq_inventory WHERE users_id = " + users_id + " AND pj_projects_id = " + projetctId +
                        " AND quantity != 0 AND deleted_at Is NULL AND eq_sub_categories_id = " + eqSubCategoryId + ")")).build();

        return query.list().size();
    }

    public int getOwnedActive(EquipmentRegion equipmentRegion, int users_id, int projetctId) {
        users_id = 0;
        List<EquipmentInventory> equipmentInventories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projetctId),
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(equipmentRegion.getEqRegionEquipentId()),
                EquipmentInventoryDao.Properties.Status.eq(1),
                EquipmentInventoryDao.Properties.EquipmentStatus.eq(1),
                EquipmentInventoryDao.Properties.DeletedAt.isNull()/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentInventories.size() > 0 && !equipmentRegion.getType().equals("Unique")) {
            int quantity = equipmentInventories.get(0).getQuantity();
            return quantity;
        } else if (equipmentRegion.getType().equals("Unique")) {
            return equipmentInventories.size();
        } else return 0;
    }

    public int getOwnedDamaged(EquipmentRegion equipmentRegion, int users_id, int projetctId) {
        List<EquipmentInventory> equipmentInventories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projetctId),
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(equipmentRegion.getEqRegionEquipentId()),
                EquipmentInventoryDao.Properties.Status.eq(2),
                EquipmentInventoryDao.Properties.EquipmentStatus.eq(1),
                EquipmentInventoryDao.Properties.DeletedAt.isNull()/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentInventories.size() > 0 && !equipmentRegion.getType().equals("Unique")) {
            int quantity = equipmentInventories.get(0).getQuantity();
            return quantity;
        } else if (equipmentRegion.getType().equals("Unique")) {
            return equipmentInventories.size();
        } else return 0;
    }

    public int getRentedActive(EquipmentRegion equipmentRegion, int users_id, int projetctId) {

        List<EquipmentInventory> equipmentInventories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projetctId),
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(equipmentRegion.getEqRegionEquipentId()),
                EquipmentInventoryDao.Properties.Status.eq(1),
                EquipmentInventoryDao.Properties.EquipmentStatus.eq(2),
                EquipmentInventoryDao.Properties.DeletedAt.isNull()/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentInventories.size() > 0 && !equipmentRegion.getType().equals("Unique")) {
            int quantity = equipmentInventories.get(0).getQuantity();
            return quantity;
        } else if (equipmentRegion.getType().equals("Unique")) {
            return equipmentInventories.size();
        } else return 0;
    }

    public int getRentedDamaged(EquipmentRegion equipmentRegion, int users_id, int projetctId) {
        users_id = 0;
        List<EquipmentInventory> equipmentInventories = getDaoSession().getEquipmentInventoryDao().queryBuilder().where(
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projetctId),
                EquipmentInventoryDao.Properties.EqRegionEquipentId.eq(equipmentRegion.getEqRegionEquipentId()),
                EquipmentInventoryDao.Properties.Status.eq(2),
                EquipmentInventoryDao.Properties.EquipmentStatus.eq(2),
                EquipmentInventoryDao.Properties.DeletedAt.isNull()/*,
                EquipmentInventoryDao.Properties.UsersId.eq(users_id)*/).list();
        if (equipmentInventories.size() > 0 && !equipmentRegion.getType().equals("Unique")) {
            int quantity = equipmentInventories.get(0).getQuantity();
            return quantity;
        } else if (equipmentRegion.getType().equals("Unique")) {
            return equipmentInventories.size();
        } else return 0;
    }
}
